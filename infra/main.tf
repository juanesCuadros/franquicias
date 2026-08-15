terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 2.16"
    }
  }
}

provider "mongodbatlas" {
  public_key  = var.atlas_public_key
  private_key = var.atlas_private_key
}

# mongodbatlas_cluster está deprecado desde la v2.0.0 del provider en favor
# de mongodbatlas_advanced_cluster (ver guía de migración oficial). Los
# clusters M0/M2/M5 son "tenant" (compartidos, sin infraestructura dedicada)
# y se describen con provider_name = "TENANT" dentro de region_configs.
resource "mongodbatlas_advanced_cluster" "cluster0" {
  project_id   = var.atlas_project_id
  name         = "Cluster0"
  cluster_type = "REPLICASET"

  replication_specs = [
    {
      region_configs = [
        {
          provider_name         = "TENANT"
          backing_provider_name = "AWS"
          region_name           = "US_EAST_1"
          priority              = 7

          electable_specs = {
            instance_size = "M0"
          }
        }
      ]
    }
  ]
}

# Permite acceso desde cualquier IP. Necesario porque Render (tier gratuito)
# no publica un rango de IPs salientes estático, así que no es posible
# restringir la lista de acceso a IPs específicas del hosting.
resource "mongodbatlas_project_ip_access_list" "allow_all" {
  project_id = var.atlas_project_id
  cidr_block = "0.0.0.0/0"
  comment    = "Acceso abierto requerido para el despliegue en Render (sin IPs salientes estáticas en el tier gratuito)"
}
