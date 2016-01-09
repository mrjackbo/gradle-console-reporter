package com.github.ksoichiro.console.reporter

import com.github.ksoichiro.console.reporter.parser.JUnitReportParser
import com.github.ksoichiro.console.reporter.report.JUnitReport
import com.github.ksoichiro.console.reporter.writer.JUnitReportWriter
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.Test

class ReportTestTask extends DefaultTask {
    public static String NAME = 'reportTest'
    ConsoleReporterExtension extension

    ReportTestTask() {
        project.afterEvaluate {
            extension = project.extensions."${ConsoleReporterExtension.NAME}"

            if (extension.junit.reportOnFailure) {
                project.gradle.taskGraph.afterTask { Task task, TaskState state ->
                    if (task instanceof Test && state.failure) {
                        execute()
                    }
                }
            }
        }
    }

    @TaskAction
    void exec() {
        if (extension.junit.enabled) {
            reportJUnit()
        }
    }

    void reportJUnit() {
        JUnitReport report = new JUnitReportParser().parse(project, extension.junit)
        new JUnitReportWriter().write(project, report, extension.junit)
    }
}
