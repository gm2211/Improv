package overtone.wrapper;

import java.io.StringReader;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Compiler;

public class OvertoneWrapper {
    public static final String USE_OVERTONE_LIVE = "(use 'overtone.live)";
    public static final String OVERTONE_LIVE = "overtone.live";
    public static final String JAVA_LIBRARY_PATH = "java.library.path";

    private OvertoneWrapperConfig config;

    public OvertoneWrapper(OvertoneWrapperConfig config) {
        this.config = config;
        initOvertoneServer();
    }

    private void initOvertoneServer() {
        System.setProperty(JAVA_LIBRARY_PATH, config.getNativeLibrariesPath());
        importLibrary(OVERTONE_LIVE);
        sendCommand(USE_OVERTONE_LIVE);
    }

    private void importLibrary(String library) {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read(library));
    }

    public void sendCommand(String command) {
        Compiler.load(new StringReader(command));
    }
}