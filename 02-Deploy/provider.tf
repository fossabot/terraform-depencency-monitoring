provider "aws" {
  region              = "${var.region}"
  allowed_account_ids = ["${var.allowed_accounts}"]

  assume_role {
    role_arn     = "${var.assume_role}"
    session_name = "Terraform"
  }
}
