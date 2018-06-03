resource "aws_s3_bucket" "terraform_dependencymonitoring_bucket" {
  bucket_prefix = "terraform-dependencymonitoring-"
  acl      = "private"
  force_destroy = true
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = "${aws_s3_bucket.terraform_dependencymonitoring_bucket.id}"

  lambda_function {
    lambda_function_arn = "${aws_lambda_function.enqueue_terraform_repos_lambda.arn}"
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "terraform/"
    filter_suffix       = ".json"
  }
}