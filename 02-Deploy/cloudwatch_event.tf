# -- Gather Terraform Modules
data "template_file" "gather_terraform_modules_cloudwatch_event_rule_template" {
  template = "${file("${path.module}/templates/lambda/gather_terraform_modules.template.json")}"

  vars {
    s3_bucket  = "${aws_s3_bucket.terraform_dependencymonitoring_bucket.bucket}"
    s3_key     = "${local.object_key_gather_terraform-modules}"
    inclusions = "${jsonencode(var.gather_terraform_modules_parameters_inclusions)}"
    exclusions = "${jsonencode(var.gather_terraform_modules_parameters_exclusions)}"
  }
}

resource "aws_cloudwatch_event_rule" "gather_terraform_modules_cloudwatch_event_rule" {
  name                = "${local.base_name_gather_terraform-modules}-rule"
  schedule_expression = "${var.gather_terraform_modules_schedule_expression}"
}

resource "aws_cloudwatch_event_target" "gather_terraform_modules_cloudwatch_event_target" {
  target_id = "${local.base_name_gather_terraform-modules}-target"
  rule      = "${aws_cloudwatch_event_rule.gather_terraform_modules_cloudwatch_event_rule.name}"
  arn       = "${aws_lambda_function.gather_terraform_modules_lambda.arn}"

  input_transformer {
    input_template = "${data.template_file.gather_terraform_modules_cloudwatch_event_rule_template.rendered}"
  }
}

# -- Github Repos
data "template_file" "gather_github_repos_cloudwatch_event_rule_template" {
  template = "${file("${path.module}/templates/lambda/gather_terraform_modules.template.json")}"

  vars {
    s3_bucket  = "${aws_s3_bucket.terraform_dependencymonitoring_bucket.bucket}"
    s3_key     = "${local.object_key_gather_github_repos}"
    inclusions = "${jsonencode(var.gather_github_repos_parameters_inclusions)}"
    exclusions = "${jsonencode(var.gather_github_repos_parameters_exclusions)}"
  }
}

resource "aws_cloudwatch_event_rule" "gather_github_repos_cloudwatch_event_rule" {
  name                = "${local.base_name_gather_github_repos}-rule"
  schedule_expression = "${var.gather_github_repos_schedule_expression}"
}

resource "aws_cloudwatch_event_target" "gather_github_repos_cloudwatch_event_target" {
  target_id = "${local.base_name_gather_github_repos}-target"
  rule      = "${aws_cloudwatch_event_rule.gather_github_repos_cloudwatch_event_rule.name}"
  arn       = "${aws_lambda_function.gather_github_repos_lambda.arn}"

  input_transformer {
    input_template = "${data.template_file.gather_github_repos_cloudwatch_event_rule_template.rendered}"
  }
}

# -- Worker Dispatcher
data "template_file" "worker_dispatcher_cloudwatch_event_rule_template" {
  template = "${file("${path.module}/templates/lambda/worker_dispatcher.template.json")}"

  vars {
    sqs_queue_name                        = "${aws_sqs_queue.project_repo_queue.name}"
    worker_reserved_concurrent_executions = "${var.worker_reserved_concurrent_executions}"
    lambda_worker_function_name           = "${aws_lambda_function.worker_lambda.function_name}"
    s3_bucket_name                        = "${aws_s3_bucket.terraform_dependencymonitoring_bucket.bucket}"
  }
}

resource "aws_cloudwatch_event_rule" "worker_dispatcher_cloudwatch_event_rule" {
  name                = "${local.base_name_worker_dispatcher}-rule"
  schedule_expression = "${var.worker_dispatcher_schedule_expression}"
}

resource "aws_cloudwatch_event_target" "worker_dispatcher_cloudwatch_event_target" {
  target_id = "${local.base_name_worker_dispatcher}-target"
  rule      = "${aws_cloudwatch_event_rule.worker_dispatcher_cloudwatch_event_rule.name}"
  arn       = "${aws_lambda_function.worker_dispatcher_lambda.arn}"

  input_transformer {
    input_template = "${data.template_file.worker_dispatcher_cloudwatch_event_rule_template.rendered}"
  }
}
