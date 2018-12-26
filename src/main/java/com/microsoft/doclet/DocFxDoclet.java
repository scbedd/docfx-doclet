package com.microsoft.doclet;

import com.microsoft.build.YmlFilesBuilder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic.Kind;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import org.apache.commons.lang3.StringUtils;

public class DocFxDoclet implements Doclet {

    private Reporter reporter;

    @Override
    public void init(Locale locale, Reporter reporter) {
        reporter.print(Kind.NOTE, "Doclet using locale: " + locale);
        this.reporter = reporter;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        if (StringUtils.isBlank(this.outputPath)) {
            reporter.print(Kind.ERROR, "Output path not specified");
            return false;
        }

        reporter.print(Kind.NOTE, "Output path: " + outputPath);
        reporter.print(Kind.NOTE, "Excluded packages: " + Arrays.toString(excludePackages));
        reporter.print(Kind.NOTE, "Excluded classes: " + Arrays.toString(excludeClasses));

        return (new YmlFilesBuilder(environment, outputPath, excludePackages, excludeClasses)).build();
    }

    @Override
    public String getName() {
        return "DocFxDoclet";
    }


    private String outputPath;
    private String[] excludePackages = {};
    private String[] excludeClasses = {};

    @Override
    public Set<? extends Option> getSupportedOptions() {
        Option[] options = {
            new CustomOption("Output path", Arrays.asList("-outputpath", "--output-path", "-o"), "path") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    outputPath = arguments.get(0);
                    return true;
                }
            },
            new CustomOption("Exclude packages", Arrays.asList("-excludepackages", "--exclude-packages", "-ep"), "packages") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    excludePackages = StringUtils.split(arguments.get(0), ":");
                    return true;
                }
            },
            new CustomOption("Exclude classes", Arrays.asList("-excludeclasses", "--exclude-classes", "-ec"), "classes") {
                @Override
                public boolean process(String option, List<String> arguments) {
                    excludeClasses = StringUtils.split(arguments.get(0), ":");
                    return true;
                }
            }
        };
        return new HashSet<>(Arrays.asList(options));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // support the latest release
        return SourceVersion.latest();
    }

    private abstract class CustomOption implements Option {

        private final String description;
        private final List<String> names;
        private final String parameters;

        public CustomOption(String description, List<String> names, String parameters) {
            this.description = description;
            this.names = names;
            this.parameters = parameters;
        }

        @Override
        public int getArgumentCount() {
            return 1;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Kind getKind() {
            return Kind.STANDARD;
        }

        @Override
        public List<String> getNames() {
            return names;
        }

        @Override
        public String getParameters() {
            return parameters;
        }
    }
}
