package com.rapidark.npm.cli;

import java.util.concurrent.Callable;

import com.rapidark.npm.JNPMService;
import com.rapidark.npm.RxJNPMService;
import com.rapidark.npm.traversal.ITraversalRule;
import com.rapidark.npm.traversal.TraversalTree;
import com.rapidark.npm.traversal.TraverseDirection;

import io.reactivex.Completable;
import io.reactivex.Observable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * Download command for JNPM CLI. Please read command description in corresponding annotation.
 */
@Command(name="download", aliases = "d", description = "Download packages into local cache")
public class DownloadCommand implements Callable<Integer>{

	protected String handledFormat = "Downloading %s@%s\n";

	@Option(names = "--download",negatable = true, description = "Download by default. Negate if  just attempt to lookup is needed")
	private boolean download = true;

	@Option(names = {"--prod"}, negatable = true, description = "Download dependencies (default)")
	private boolean getProd = true;
    @Option(names = {"--dev"}, negatable = true, description = "Download dev dependencies")
    private boolean getDev = false;
    @Option(names = {"--optional"},negatable = true,  description = "Download optional dependencies")
    private boolean getOptional = false;
    @Option(names = {"--peer"},negatable = true,  description = "Download peer dependencies")
    private boolean getPeer = false;

    @Option(names = {"-h", "--help"}, usageHelp = true)
    private boolean usageHelp;

	@Parameters(description = "Packages to be retrieved", arity = "1..*")
    private String[] packageStatements;


	@ParentCommand
    protected JNPM parent;

	@Override
	public Integer call() throws Exception {
		parent.configure();
		RxJNPMService rxService = JNPMService.instance().getRxService();
		ITraversalRule rule = ITraversalRule.getRuleFor(getProd, getDev, getOptional, getPeer);
		Observable<TraversalTree> observable = rxService.traverse(TraverseDirection.WIDER, rule, packageStatements)
				.doOnNext(t->System.out.printf("Downloading %s@%s\n", t.getVersion().getName(), t.getVersion().getVersionAsString()));
		if(download) {
			observable.flatMapCompletable(this::doAction).blockingAwait();
		} else {
			observable.ignoreElements().blockingAwait();
		}
		return 0;
	}

	protected Completable doAction(TraversalTree tree) {
		return tree.getVersion().downloadTarball();
	}

}
