locals {
  # -- Generic values
  default_lambda_tag_value = "terraform-dependency-monitoring"

  # -- Gather Terraform Modules
  base_name_gather_terraform-modules = "tfdepmon-gather-terraform-modules"
  lambda_name_gather_terraform-modules = "${local.base_name_gather_terraform-modules}-function"
  lambda_artifact_gather_terraform-modules = "${path.module}/../01-SourceCode/01-Lambda/terraform-dependency-monitoring/tfdepmon-gather-terraform-modules/target/tfdepmon-gather-terraform-modules.jar"
  role_name_gather_terraform-modules = "${local.base_name_gather_terraform-modules}-role"
  object_key_gather_terraform-modules = "terraform/modules/info.json"

  # -- Github Repos
  base_name_gather_github_repos = "tfdepmon-gather-github-repos"
  lambda_name_gather_github_repos = "${local.base_name_gather_github_repos}-function"
  lambda_artifact_gather_github_repos = "${path.module}/../01-SourceCode/01-Lambda/terraform-dependency-monitoring/tfdepmon-gather-github-repos/target/tfdepmon-gather-github-repos.jar"
  role_name_gather_github_repos = "${local.base_name_gather_github_repos}-role"
  object_key_gather_github_repos = "terraform/project_repos/info.json"

  # -- Enqueue Terraform Repos
  base_name_enqueue_terraform_repos = "tfdepmon-enqueue-terraform-repos"
  lambda_name_enqueue_terraform_repos = "${local.base_name_enqueue_terraform_repos}-function"
  lambda_artifact_enqueue_terraform_repos = "${path.module}/../01-SourceCode/01-Lambda/terraform-dependency-monitoring/tfdepmon-enqueue-terraform-repos/target/tfdepmon-enqueue-terraform-repos.jar"
  role_name_enqueue_terraform = "${local.base_name_enqueue_terraform_repos}-role"

  # -- Worker Dispatcher
  base_name_worker_dispatcher = "tfdepmon-worker-dispatcher"
  lambda_name_worker_dispatcher = "${local.base_name_worker_dispatcher}-function"
  lambda_artifact_worker_dispatcher = "${path.module}/../01-SourceCode/01-Lambda/terraform-dependency-monitoring/tfdepmon-worker-dispatcher/target/tfdepmon-worker-dispatcher.jar"
  role_name_worker_dispatcher = "${local.base_name_worker_dispatcher}-role"

  # -- Worker
  base_name_worker = "tfdepmon-worker"
  lambda_name_worker = "${local.base_name_worker}-function"
  lambda_artifact_worker = "${path.module}/../01-SourceCode/01-Lambda/terraform-dependency-monitoring/tfdepmon-worker/target/tfdepmon-worker.jar"
  role_name_worker = "${local.base_name_worker}-role"
}