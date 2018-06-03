resource "aws_kms_key" "key" {
  enable_key_rotation = "${var.kms_enable_key_rotation}"
  policy = "${data.aws_iam_policy_document.kms_key_policy.json}"
}

resource "aws_kms_alias" "alias_key" {
  name          = "alias/terraform-depedencymonitoring"
  target_key_id = "${aws_kms_key.key.key_id}"
}