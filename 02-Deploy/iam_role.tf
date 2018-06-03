# -- Generic
resource "aws_iam_policy_attachment" "basic-policy-attachement" {
  name       = "basic-lambda-policy-attachment"
  roles      = [
    "${aws_iam_role.enqueue_terraform_repos_role.name}",
    "${aws_iam_role.gather_github_repos_role.name}",
    "${aws_iam_role.gather_terraform_modules_role.name}",
    "${aws_iam_role.worker_dispatcher_role.name}",
    "${aws_iam_role.worker_role.name}",
  ]
  policy_arn = "${aws_iam_policy.basic_lambda_policy.arn}"
}

# -- Gather Terraform Modules
resource "aws_iam_role" "gather_terraform_modules_role" {
  name = "${local.role_name_gather_terraform-modules}"
  assume_role_policy = "${data.aws_iam_policy_document.lambda_assume_role.json}"
}

# -- Github Repos
resource "aws_iam_role" "gather_github_repos_role" {
  name = "${local.role_name_gather_github_repos}"
  assume_role_policy = "${data.aws_iam_policy_document.lambda_assume_role.json}"
}

# -- Enqueue Terraform Repos
resource "aws_iam_role" "enqueue_terraform_repos_role" {
  name = "${local.role_name_enqueue_terraform}"
  assume_role_policy = "${data.aws_iam_policy_document.lambda_assume_role.json}"
}

resource "aws_iam_policy_attachment" "enqueue_terraform_repos_role_attach_sqs_policy" {
  name       = "enqueue_terraform_repos_role_attach_sqs_policy"
  roles      = ["${aws_iam_role.enqueue_terraform_repos_role.name}"]
  policy_arn = "${aws_iam_policy.basic_send_sqs_messages_policy.arn}"
}

# -- Worker Dispatcher
resource "aws_iam_role" "worker_dispatcher_role" {
  name = "${local.role_name_worker_dispatcher}"
  assume_role_policy = "${data.aws_iam_policy_document.lambda_assume_role.json}"
}

resource "aws_iam_policy_attachment" "worker_dispatcher_role_attach_sqs_policy" {
  name       = "worker_dispatcher_role_attach_sqs_policy"
  roles      = ["${aws_iam_role.worker_dispatcher_role.name}"]
  policy_arn = "${aws_iam_policy.basic_receive_sqs_messages_policy.arn}"
}

resource "aws_iam_policy_attachment" "worker_dispatcher_role_attach_invoke_policy" {
  name       = "worker_dispatcher_role_attach_invoke_policy"
  roles      = ["${aws_iam_role.worker_dispatcher_role.name}"]
  policy_arn = "${aws_iam_policy.lambda_worker_dispatcher_policy.arn}"
}

# -- Worker
resource "aws_iam_role" "worker_role" {
  name = "${local.role_name_worker}"
  assume_role_policy = "${data.aws_iam_policy_document.lambda_assume_role.json}"
}

resource "aws_iam_policy_attachment" "worker_role_attach_sqs_policy" {
  name       = "worker_role_attach_sqs_policy"
  roles      = ["${aws_iam_role.worker_role.name}"]
  policy_arn = "${aws_iam_policy.basic_delete_sqs_messages_policy.arn}"
}