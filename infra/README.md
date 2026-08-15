# Infraestructura como código (Terraform)

Este Terraform describe el cluster de MongoDB Atlas usado por `franquicias-api` en producción: un cluster `Cluster0` de tier **M0** (compartido/tenant, gratuito) en AWS `US_EAST_1`, más la lista de acceso IP que permite `0.0.0.0/0` (requerida porque Render, en su tier gratuito, no publica un rango de IPs salientes estático).

## Estado actual: documentado, no aplicado

**Este código todavía no se corrió con `terraform apply` contra el proyecto de Atlas productivo.** El cluster y la lista de acceso ya existen porque se crearon manualmente desde la consola web de Atlas mientras se armaba el despliegue. Este `infra/` se agregó después, como IaC para dejar la infraestructura documentada y reproducible — no para reemplazar lo que ya está corriendo.

Dos caminos posibles a futuro, sin apurarlos:

- **Dejarlo solo como documentación / plan de referencia**: sirve para que cualquiera pueda ver exactamente qué configuración tiene el cluster y recrearla desde cero si hiciera falta (por ejemplo, en otro proyecto o entorno), sin tener que tocar el cluster actual.
- **Adoptar el recurso existente con `terraform import`**, para que Terraform empiece a gestionar el cluster real:
  ```
  terraform import mongodbatlas_advanced_cluster.cluster0 <project_id>-Cluster0
  terraform import mongodbatlas_project_ip_access_list.allow_all <project_id>-0.0.0.0%2F0
  ```
  Después de importar, hay que correr `terraform plan` y confirmar que sale **sin diffs** (o revisar con cuidado cualquier diferencia) antes de considerar que el estado de Terraform realmente refleja el cluster real. Recién ahí sería seguro volver a correr `apply` sobre él.

Mientras no se haga el import, **no correr `terraform apply`** con este código: al no haber estado (`.tfstate`) que referencie el cluster existente, Terraform intentaría crear un cluster `Cluster0` nuevo (probablemente fallando por nombre duplicado, pero sin esa garantía) en vez de gestionar el actual.

## Cómo correrlo desde cero

Prerrequisitos: [Terraform](https://developer.hashicorp.com/terraform/install) instalado, y un API key de Atlas (público/privado) con permisos sobre el proyecto — se genera en **Organization Access Manager > API Keys** en la consola de Atlas.

1. Copiar el archivo de ejemplo de variables:
   ```
   cp terraform.tfvars.example terraform.tfvars
   ```
2. Completar `terraform.tfvars` con tus credenciales reales (`atlas_public_key`, `atlas_private_key`, `atlas_project_id`). Este archivo está en `.gitignore` — nunca se commitea.
3. Inicializar el provider:
   ```
   terraform init
   ```
4. Ver el plan de ejecución (no modifica nada en Atlas, solo muestra qué haría):
   ```
   terraform plan
   ```
5. Recién si el plan se ve como se espera (y, si corresponde, después de hacer el `import` descrito arriba), aplicar:
   ```
   terraform apply
   ```
