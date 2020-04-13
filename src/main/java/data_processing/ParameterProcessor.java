package data_processing;

import java.util.*;
import java.util.Map.Entry;

//From CODes
public class ParameterProcessor {
    private List<String> rawArgs = new ArrayList();
    private Map<String, String> namedParams = new HashMap();
    private List<String> unnamedParams = new ArrayList();
    private List<String> readonlyRawArgs = null;
    private Map<String, String> readonlyNamedParams = null;
    private List<String> readonlyUnnamedParams = null;

    public ParameterProcessor() {

    }

    public ParameterProcessor(List<String> args) {
        if (args != null) {
            this.init(args);
        }

    }

    public ParameterProcessor(String[] args) {
        if (args != null) {
            this.init(Arrays.asList(args));
        }

    }

    public ParameterProcessor(Map params, String[] arguments) {
        this.init(params, arguments);
    }

    private void init(List<String> args) {
        Iterator var2 = args.iterator();

        while (var2.hasNext()) {
            String arg = (String) var2.next();
            if (arg != null) {
                this.rawArgs.add(arg);
            }
        }

        this.computeNamedParams();
        this.computeUnnamedParams();
    }

    private void init(Map params, String[] arguments) {
        Iterator var3 = params.entrySet().iterator();

        while (var3.hasNext()) {
            Object e = var3.next();
            Object key = ((Entry) e).getKey();
            if (this.validKey(key)) {
                Object value = params.get(key);
                if (value instanceof String) {
                    this.namedParams.put((String) key, (String) value);
                }
            }
        }

        this.computeRawArgs();
        if (arguments != null) {
            String[] var7 = arguments;
            int var8 = arguments.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                String arg = var7[var9];
                this.unnamedParams.add(arg);
                this.rawArgs.add(arg);
            }
        }

    }

    private boolean validFirstChar(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean validKey(Object key) {
        if (key instanceof String) {
            String keyStr = (String) key;
            if (keyStr.length() > 0 && keyStr.indexOf(61) < 0) {
                return this.validFirstChar(keyStr.charAt(0));
            }
        }

        return false;
    }

    private boolean isNamedParam(String arg) {
        if (!arg.startsWith("--")) {
            return false;
        } else {
            return arg.indexOf(61) > 2 && this.validFirstChar(arg.charAt(2));
        }
    }

    private void computeUnnamedParams() {
        Iterator var1 = this.rawArgs.iterator();

        while (var1.hasNext()) {
            String arg = (String) var1.next();
            if (!this.isNamedParam(arg)) {
                this.unnamedParams.add(arg);
            }
        }

    }

    private void computeNamedParams() {
        Iterator var1 = this.rawArgs.iterator();

        while (var1.hasNext()) {
            String arg = (String) var1.next();
            if (this.isNamedParam(arg)) {
                int eqIdx = arg.indexOf(61);
                String key = arg.substring(2, eqIdx);
                String value = arg.substring(eqIdx + 1);
                this.namedParams.put(key, value);
            }
        }

    }

    private void computeRawArgs() {
        ArrayList<String> keys = new ArrayList();
        keys.addAll(this.namedParams.keySet());
        Collections.sort(keys);
        Iterator var2 = keys.iterator();

        while (var2.hasNext()) {
            String key = (String) var2.next();
            this.rawArgs.add("--" + key + "=" + this.namedParams.get(key));
        }

    }

    public List<String> getRaw() {
        if (this.readonlyRawArgs == null) {
            this.readonlyRawArgs = Collections.unmodifiableList(this.rawArgs);
        }

        return this.readonlyRawArgs;
    }

    public Map<String, String> getNamed() {
        if (this.readonlyNamedParams == null) {
            this.readonlyNamedParams = Collections.unmodifiableMap(this.namedParams);
        }

        return this.readonlyNamedParams;
    }

    public List<String> getUnnamed() {
        if (this.readonlyUnnamedParams == null) {
            this.readonlyUnnamedParams = Collections.unmodifiableList(this.unnamedParams);
        }

        return this.readonlyUnnamedParams;
    }

}
