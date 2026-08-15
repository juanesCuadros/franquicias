variable "atlas_public_key" {
  description = "Clave pública (API key) de MongoDB Atlas, generada en Organization Access Manager > API Keys. Se usa para autenticar al provider de Terraform contra la Atlas Admin API."
  type        = string
  sensitive   = true
}

variable "atlas_private_key" {
  description = "Clave privada (API key) de MongoDB Atlas, asociada a atlas_public_key. Nunca debe hardcodearse ni commitearse -- se provee vía terraform.tfvars (ignorado por git) o una variable de entorno TF_VAR_atlas_private_key."
  type        = string
  sensitive   = true
}

variable "atlas_project_id" {
  description = "ID del proyecto de MongoDB Atlas (Project ID, visible en Project Settings de la consola de Atlas) donde vive el cluster Cluster0 y la lista de acceso IP."
  type        = string
}
