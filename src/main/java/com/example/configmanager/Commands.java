package com.example.configmanager;

import com.pastdev.jsch.DefaultSessionFactory;
import com.pastdev.jsch.command.CommandRunner;
import com.pastdev.jsch.scp.ScpFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;


@ShellComponent
public class Commands {

    private final ConsoleService console;
    private final ConfigManagerService service;
    private final Config config;

    public Commands(ConsoleService console, Config config, ConfigManagerService service) {
        this.console = console;
        this.config = config;
        this.service = service;
    }

    @ShellMethod("show config")
    public void showConfig() {
        this.console.write("Config: %s", config.toString());
        this.console.write("Service: %s", service.toString());
    }

    @ShellMethod("run packages")
    public void runPackages() {
        this.console.write("Running packages on remote machine");

        // TODO: can run multiple threads using Runnable
        for (String host : config.getHosts()) {
            DefaultSessionFactory sessionFactory = service.getSessionFactory(host);
            CommandRunner runner = new CommandRunner(sessionFactory);
            CommandRunner.ExecuteResult result = null;

            for (PackageConfig packageConfig : config.getPackages()) {
                // TODO: use switch statement
                if (packageConfig.state.equals("present")) {
                    this.console.write("Installing package %s on %s", packageConfig.name, host);
                    try {
                        String command = "apt-get update" + " && " +
                                "apt-get --yes install " + packageConfig.name;
                        this.console.write("Running command: %s on %s", command, host);
                        result = runner.execute(command);

                        if (result.getStderr().isEmpty()) {
                            this.console.write("Success: %s", result.getStdout());
                        } else {
                            this.console.write("Error: %s", result.getStderr());
                        }

                        runner.close();
                    } catch (Exception exec) {
                        exec.printStackTrace();
                    }
                } else if(packageConfig.state.equals("enabled")) {
                    this.console.write("Installing and enabling %s on %s", packageConfig.name, host);
                    try {
                        String command = "apt-get update" + " && " +
                                "apt-get --yes install " + packageConfig.name + " && " +
                                "service " + packageConfig.name + " start";
                        this.console.write("Running command: %s on %s", command, host);
                        result = runner.execute(command);

                        if (result.getStderr().isEmpty()) {
                            this.console.write("Success: %s", result.getStdout());
                        } else {
                            this.console.write("Error: %s", result.getStderr());
                        }

                        runner.close();
                    } catch (Exception exec) {
                        exec.printStackTrace();
                    }
                } else if (packageConfig.state.equals("absent")) {
                    this.console.write("Removing package %s on %s", packageConfig.name, host);
                    try {
                        String command = "apt-get --yes purge " + packageConfig.name + " && " +
                                "apt-get --yes autoremove";
                        this.console.write("Running command: %s on %s", command, host);
                        result = runner.execute(command);

                        if (result.getStderr().isEmpty()) {
                            this.console.write("Success: %s", result.getStdout());
                        } else {
                            this.console.write("Error: %s", result.getStderr());
                        }

                        runner.close();
                    } catch (Exception exec) {
                        exec.printStackTrace();
                    }
                } else {
                    this.console.write("Invalid config: %s", packageConfig.state);
                }
            }
        }
    }

    @ShellMethod("copy files")
    public void copyFiles() {
        this.console.write("Copying files to remote machine");

        for (String host : config.getHosts()) {
            this.console.write("Copying files to remote machine: %s", host);
            DefaultSessionFactory sessionFactory = service.getSessionFactory(host);
            CommandRunner runner = new CommandRunner(sessionFactory);
            CommandRunner.ExecuteResult result = null;

            for (FileConfig fileConfig : config.getFiles()) {
                try {
                    this.console.write("Copying %s to %s", fileConfig.getSrc(), host);
                    ScpFile to = new ScpFile(sessionFactory, fileConfig.getDest(), fileConfig.getFilename());
                    to.copyFrom(new File(fileConfig.getSrc()), fileConfig.getMode());

                    String destinationFile = fileConfig.getDest() + "/" + fileConfig.getFilename();

                    this.console.write("Changing file ownership of %s", destinationFile);
                    String  command = "chown " + fileConfig.getOwner() + ":" + fileConfig.getGroup() + " " + destinationFile;
                    result = runner.execute(command);

                    if (result.getStderr().isEmpty()) {
                        this.console.write("Changing file ownership success: %s", result.getStdout());
                    } else {
                        this.console.write("Changing file ownership error: %s", result.getStderr());
                    }

                    this.console.write("Applying file permissions to %s", destinationFile);
                    command = "chmod " + fileConfig.getMode() + " " + destinationFile;
                    result = runner.execute(command);

                    if (result.getStderr().isEmpty()) {
                        this.console.write("Applying file permissions success: %s", result.getStdout());
                    } else {
                        this.console.write("Applying file permissions rrror: %s", result.getStderr());
                    }

                    if (fileConfig.getRestart() != null) {
                        for (String packageName : fileConfig.getRestart()) {

                            this.console.write("Restarting service %s", packageName);
                            command = "service " + packageName + " restart";
                            result = runner.execute(command);

                            if (result.getStderr().isEmpty()) {
                                this.console.write("Restarting service success: %s", result.getStdout());
                            } else {
                                this.console.write("Restarting service error: %s", result.getStderr());
                            }
                        }
                    }

                    runner.close();
                } catch (Exception exec) {
                    exec.printStackTrace();
                }
            }
        }
    }

}
