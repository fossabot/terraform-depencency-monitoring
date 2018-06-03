# -- Common data
data "aws_kms_ciphertext" "git_username_encrypted" {
  key_id    = "${aws_kms_key.key.key_id}"
  plaintext = "${var.git_username}"
}

data "aws_kms_ciphertext" "git_password_encrypted" {
  key_id    = "${aws_kms_key.key.key_id}"
  plaintext = "${var.git_password}"
}

# -- Gather Terraform Modules
resource "aws_lambda_function" "gather_terraform_modules_lambda" {
  filename         = "${local.lambda_artifact_gather_terraform-modules}"
  source_code_hash = "${base64sha256(file(local.lambda_artifact_gather_terraform-modules))}"
  function_name    = "${local.lambda_name_gather_terraform-modules}"
  role             = "${aws_iam_role.gather_terraform_modules_role.arn}"
  description      = "Function that gather terraform modules from Github"
  handler          = "com.github.dependencymonitoring.terraform.gatherterraformmodules.main.Main::handleRequest"
  runtime          = "${var.lambda_default_runtime}"
  memory_size      = "${var.lambda_default_memory_size}"
  timeout          = "${var.lambda_default_timeout}"

  environment {
    variables {
      "git_username" = "${data.aws_kms_ciphertext.git_username_encrypted.ciphertext_blob}"
      "git_password" = "${data.aws_kms_ciphertext.git_password_encrypted.ciphertext_blob}"
    }
  }

  tags {
    SystemName = "${local.default_lambda_tag_value}"
  }
}

resource "aws_lambda_permission" "gather_terraform_modules_permission" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.gather_terraform_modules_lambda.function_name}"
  principal     = "events.amazonaws.com"
  source_arn    = "${aws_cloudwatch_event_rule.gather_terraform_modules_cloudwatch_event_rule.arn}"
}

# -- Github Repos
resource "aws_lambda_function" "gather_github_repos_lambda" {
  filename         = "${local.lambda_artifact_gather_github_repos}"
  source_code_hash = "${base64sha256(file(local.lambda_artifact_gather_github_repos))}"
  function_name    = "${local.lambda_name_gather_github_repos}"
  role             = "${aws_iam_role.gather_github_repos_role.arn}"
  description      = "Function that gather Github repos"
  handler          = "com.github.dependencymonitoring.terraform.gathergithubrepos.main.Main::handleRequest"
  runtime          = "${var.lambda_default_runtime}"
  memory_size      = "${var.lambda_default_memory_size}"
  timeout          = "${var.lambda_default_timeout}"

  environment {
    variables {
      "git_username" = "${data.aws_kms_ciphertext.git_username_encrypted.ciphertext_blob}"
      "git_password" = "${data.aws_kms_ciphertext.git_password_encrypted.ciphertext_blob}"
    }
  }

  tags {
    SystemName = "${local.default_lambda_tag_value}"
  }
}

resource "aws_lambda_permission" "gather_github_repos_permission" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.gather_github_repos_lambda.function_name}"
  principal     = "events.amazonaws.com"
  source_arn    = "${aws_cloudwatch_event_rule.gather_github_repos_cloudwatch_event_rule.arn}"
}

# -- Enqueue Terraform Repos
resource "aws_lambda_function" "enqueue_terraform_repos_lambda" {
  filename         = "${local.lambda_artifact_enqueue_terraform_repos}"
  source_code_hash = "${base64sha256(file(local.lambda_artifact_enqueue_terraform_repos))}"
  function_name    = "${local.lambda_name_enqueue_terraform_repos}"
  role             = "${aws_iam_role.enqueue_terraform_repos_role.arn}"
  description      = "Function that read files from S3 and enqueue messages to SQS"
  handler          = "com.github.dependencymonitoring.terraform.enqueueterraformrepos.main.Main::handleRequest"
  runtime          = "${var.lambda_default_runtime}"
  memory_size      = "${var.lambda_default_memory_size}"
  timeout          = "${var.lambda_default_timeout}"

  environment {
    variables {
      "bucket_name"       = "${aws_s3_bucket.terraform_dependencymonitoring_bucket.bucket}"
      "modules_key"       = "${local.object_key_gather_terraform-modules}"
      "project_repos_key" = "${local.object_key_gather_github_repos}"
      "sqs_queue_name"    = "${aws_sqs_queue.project_repo_queue.name}"
    }
  }

  tags {
    SystemName = "${local.default_lambda_tag_value}"
  }
}

resource "aws_lambda_permission" "enqueue_terraform_repos_permission" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.enqueue_terraform_repos_lambda.arn}"
  principal     = "s3.amazonaws.com"
  source_arn    = "${aws_s3_bucket.terraform_dependencymonitoring_bucket.arn}"
}

# -- Worker Dispatcher
resource "aws_lambda_function" "worker_dispatcher_lambda" {
  filename         = "${local.lambda_artifact_worker_dispatcher}"
  source_code_hash = "${base64sha256(file(local.lambda_artifact_worker_dispatcher))}"
  function_name    = "${local.lambda_name_worker_dispatcher}"
  role             = "${aws_iam_role.worker_dispatcher_role.arn}"
  description      = "Function that dispatches multiple worker lambda functions for analysis"
  handler          = "com.github.dependencymonitoring.terraform.workerdispatcher.main.Main::handleRequest"
  runtime          = "${var.lambda_default_runtime}"
  memory_size      = "${var.lambda_default_memory_size}"
  timeout          = "${var.lambda_default_timeout}"

  tags {
    SystemName = "${local.default_lambda_tag_value}"
  }
}

resource "aws_lambda_permission" "worker_dispatcher_permission" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.worker_dispatcher_lambda.function_name}"
  principal     = "events.amazonaws.com"
  source_arn    = "${aws_cloudwatch_event_rule.worker_dispatcher_cloudwatch_event_rule.arn}"
}

# -- Worker
resource "aws_lambda_function" "worker_lambda" {
  filename         = "${local.lambda_artifact_worker}"
  source_code_hash = "${base64sha256(file(local.lambda_artifact_worker))}"
  function_name    = "${local.lambda_name_worker}"
  role             = "${aws_iam_role.worker_role.arn}"
  description      = "Function that analizes the project repo and open PRs to GitHub"
  handler          = "com.github.dependencymonitoring.terraform.worker.main.Main::handleRequest"
  runtime          = "${var.lambda_default_runtime}"
  memory_size      = "${var.worker_memory_size}"
  timeout          = "${var.worker_timeout}"

  environment {
    variables {
      "git_username" = "${data.aws_kms_ciphertext.git_username_encrypted.ciphertext_blob}"
      "git_password" = "${data.aws_kms_ciphertext.git_password_encrypted.ciphertext_blob}"
    }
  }

  tags {
    SystemName = "${local.default_lambda_tag_value}"
  }
}