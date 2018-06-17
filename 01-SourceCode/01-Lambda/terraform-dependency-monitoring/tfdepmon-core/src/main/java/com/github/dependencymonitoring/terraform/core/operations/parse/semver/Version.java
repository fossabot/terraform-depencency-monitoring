package com.github.dependencymonitoring.terraform.core.operations.parse.semver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.github.dependencymonitoring.terraform.core.exceptions.InvalidVersionException;

import java.io.IOException;

/**
 * Class the represents the semantic version in the /VERSION file
 *
 * @author <a href="mailto:paulo.miguel.almeida.rodenas@gmail.com">Paulo Miguel Almeida</a>
 */
@JsonSerialize(using = VersionJSONCustomSerializer.class)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Version implements Cloneable,Comparable<Version>{
    /**
     * major version instance
     */
    @Getter private int major;
    /**
     * minor version instance
     */
    @Getter private int minor;
    /**
     * fix version instance
     */
    @Getter private int fix;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Transform the Version instance into String
     *
     * @return String representation of the semantic versioning
     */
    public String format() {
        return String.format("v%d.%d.%d", major, minor, fix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Version o) {
        if(this.major > o.major) return 1;
        else if(this.major < o.major) return -1;
        else{
            if(this.minor > o.minor) return 1;
            else if(this.minor < o.minor) return -1;
            else{
                return Integer.compare(this.fix, o.fix);
            }
        }
    }

    /**
     * Method used by jackson-json framework when parsing json into Version object
     * @param versionStr - semantic version
     * @return Version instance reference
     * @throws InvalidVersionException - in case the version found doesn't meet the criteria
     */
    @JsonCreator
    public static Version factory(String versionStr) throws InvalidVersionException {
        return new VersionParseOperation(versionStr).performOperation();
    }

}

/**
 * Helper class used by jackson-json framework that instructs it how to convert it to JSON values.
 */
class VersionJSONCustomSerializer extends StdSerializer<Version> {

    /**
     * Default constructor
     */
    public VersionJSONCustomSerializer(){
        super(Version.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Version value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.format());
    }
}
