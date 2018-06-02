package com.github.dependencymonitoring.terraform.core.operations.parse.semver;


import com.github.dependencymonitoring.terraform.core.operations.ITypedOperation;
import lombok.AllArgsConstructor;
import com.github.dependencymonitoring.terraform.core.exceptions.InvalidVersionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.dependencymonitoring.terraform.core.utils.ICoreConstants.VERSION_REGEX_PATTERN;


/**
 * Class that represents the o
 * @author  <a href="mailto:paulo.almeida@soltius.co.nz">Paulo Miguel Almeida</a>
 *
 */
@AllArgsConstructor
public class VersionParseOperation implements ITypedOperation<Version> {

    private String versionStr;

    @Override
    public Version performOperation() throws InvalidVersionException {
        Pattern pattern = Pattern.compile(VERSION_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(this.versionStr);

        Version returnVersion = null;

        while (matcher.find()) {
            returnVersion = new Version(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3))
            );
        }

        if(returnVersion == null)
            throw new InvalidVersionException(this.versionStr);

        return returnVersion;
    }

    public static class VersionFormatter {

        public static String format(Version version){
            return format(version, false);
        }

        public static String format(Version version, boolean beta){
            return beta ? version.format() + "-beta" : version.format();
        }
    }
}

