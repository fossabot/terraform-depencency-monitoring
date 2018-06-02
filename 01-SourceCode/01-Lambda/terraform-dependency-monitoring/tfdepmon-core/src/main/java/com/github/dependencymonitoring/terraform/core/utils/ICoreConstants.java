package com.github.dependencymonitoring.terraform.core.utils;

/**
 *
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
public interface ICoreConstants {

    String VERSION_REGEX_PATTERN = "v(\\d+)\\.(\\d+)\\.(\\d+)";
    int LOGICAL_LOCK_S3_INTERVAL_IN_MIN = 60;
}
