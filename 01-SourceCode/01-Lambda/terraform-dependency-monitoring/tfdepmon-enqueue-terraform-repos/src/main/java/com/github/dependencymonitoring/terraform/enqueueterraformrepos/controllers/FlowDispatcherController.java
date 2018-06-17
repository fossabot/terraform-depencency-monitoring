package com.github.dependencymonitoring.terraform.enqueueterraformrepos.controllers;

import com.github.dependencymonitoring.terraform.core.controllers.BaseController;
import com.github.dependencymonitoring.terraform.core.exceptions.ControllerException;

/**
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 *
 */
public class FlowDispatcherController extends BaseController {

    @Override
    public void execute() throws ControllerException {
        new MessageSchedulerController().execute();
    }

}
