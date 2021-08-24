package com.tzj.providers;

import android.content.pm.PackageManager;
import android.os.Binder;

import java.io.FileDescriptor;
import java.io.PrintWriter;

final public class MemoryService extends Binder {
    private final MemoryProvider mProvider;

    public MemoryService(MemoryProvider provider) {
        mProvider = provider;
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (mProvider.getContext().checkCallingPermission(android.Manifest.permission.DUMP)
                != PackageManager.PERMISSION_GRANTED) {
            pw.println("Permission Denial: can't dump MemoryKVService from from pid="
                    + Binder.getCallingPid()
                    + ", uid=" + Binder.getCallingUid()
                    + " without permission "
                    + android.Manifest.permission.DUMP);
            return;
        }

        int opti = 0;
        while (opti < args.length) {
            String opt = args[opti];
            if (opt == null || opt.length() <= 0 || opt.charAt(0) != '-') {
                break;
            }
            opti++;
            if ("-h".equals(opt)) {
                dumpHelp(pw, false);
                return;
            } else {
                pw.println("Unknown argument: " + opt + "; use -h for help");
            }
        }

        final long ident = Binder.clearCallingIdentity();
        try {
            if (args.length == 0 || (args.length == 1 && "list".equals(args[0]))) {
                MemoryProvider.dumpAll(pw);
            } else {
                switch (args[0]) {
                    case "get":
                        if (args.length > 1 && args[1] != null) {
                            pw.println(MemoryProvider.get(args[1]));
                            return;
                        }
                        break;
                    case "put":
                        if (args.length > 2 && args[1] != null && args[2] != null) {
                            pw.println("Put " + MemoryProvider.put(mProvider.getContext(), args[1], args[2]));
                            return;
                        }
                        break;
                    case "delete":
                        if (args.length > 1 && args[1] != null) {
                            pw.println("Deleted " + MemoryProvider.delete(mProvider.getContext(), args[1]));
                            return;
                        }
                        break;
                    default:
                }
                pw.println("Unspecified command");
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    static void dumpHelp(PrintWriter pw, boolean dumping) {
        if (dumping) {
            pw.println("Memory provider dump options:");
            pw.println("  [-h]");
            pw.println("  -h: print this help.");
        } else {
            pw.println("Memory provider commands:");
            pw.println("  -h");
            pw.println("      Print this help text.");
            pw.println("  get KEY");
            pw.println("      Retrieve the current value of KEY.");
            pw.println("  put KEY VALUE");
            pw.println("      Change the contents of KEY to VALUE.");
            pw.println("  delete KEY");
            pw.println("      Delete the entry for KEY.");
            pw.println("  list");
            pw.println("      Print all defined keys.");
        }
    }
}
