#########################################
# Populate AWS provider here            #
#########################################
# -- Provider
#region = "ap-southeast-2"
#assume_role = "arn:aws:iam::12345678901:role/Role_I_Need_To_Assume_In_Order_To_Get_Access_To_An_AWS_Account"
#allowed_accounts = "12345678901"


#####################################################
# Populate Gather Terraform Modules Paramaters here #
#####################################################
# -- Lambda - gather_terraform_modules specific
#gather_terraform_modules_parameters_inclusions = [
#  "terraform-mod", # It means that repos the begins with terraform-mod-* will be analysed as source repos
#]
#gather_terraform_modules_parameters_exclusions = [
#  "terraform-mod-awsconfig" # It means that this specific repo won't be taken into consideration when evaluating upgrades
#]

# -- CloudWatch - gather_terraform_modules specific
gather_terraform_modules_schedule_expression = "cron(0 19 ? * SUN-THU *)"

#################################################
# Populate Gather Project Repos Parameters here #
#################################################
# -- Lambda - gather github repos specific
#gather_github_repos_parameters_inclusions = []
#gather_github_repos_parameters_exclusions = [
  # It means that repos with either of theses naming conventions won't be analysed
  # to check whether or not they use terraform modules. This isn't mandatory but
  # it certainly speeds up the analisys.
#  "chef-", "cordova", "datadog-",
#]

# -- CloudWatch - gather github repos specific
gather_github_repos_schedule_expression = "cron(15 19 ? * SUN-THU *)"

# -- CloudWatch - worker dispatcher specific
worker_dispatcher_schedule_expression = "rate(5 minutes)"

#########################################
# Populate your github credentials here #
#########################################
# -- Git
#git_username = "Your-Github-Username"
#git_password = "Your-Github-Password"
