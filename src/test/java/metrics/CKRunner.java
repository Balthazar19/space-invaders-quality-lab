package metrics;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKMethodResult;
import com.github.mauricioaniche.ck.CKNotifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Minimal CK runner to extract CK/CKJM metrics and compute a simple MI' approximation (without Halstead).
 * No changes to production code – only writes CSVs to target/ck.
 */
public class CKRunner {
    public static void main(String[] args) throws Exception {
        String src = args.length > 0 ? args[0] : "src/main/java";
        String out = args.length > 1 ? args[1] : "target/ck";

        Path outDir = Paths.get(out);
        Files.createDirectories(outDir);

        List<CKClassResult> results = new ArrayList<>();

        CK ck = new CK(true, 0, false);
        ck.calculate(src, new CKNotifier() {
            @Override
            public void notify(CKClassResult result) {
                results.add(result);
            }
        });

        writeClassCsv(outDir.resolve("class.csv").toFile(), results);
        writeMethodCsv(outDir.resolve("method.csv").toFile(), results);
        writeMiCsv(outDir.resolve("mi_summary.csv").toFile(), results);
    }

    private static void writeClassCsv(File file, List<CKClassResult> results) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("class,file,loc,wmc,cbo,rfc,dit,noc,lcom,tcc,lcc\n");
            for (CKClassResult r : results) {
                w.write(escape(r.getClassName())); w.write(',');
                w.write(escape(r.getFile())); w.write(',');
                w.write(Integer.toString(r.getLoc())); w.write(',');
                w.write(Integer.toString(r.getWmc())); w.write(',');
                w.write(Integer.toString(r.getCbo())); w.write(',');
                w.write(Integer.toString(r.getRfc())); w.write(',');
                w.write(Integer.toString(r.getDit())); w.write(',');
                w.write(Integer.toString(r.getNoc())); w.write(',');
                w.write(Integer.toString(r.getLcom())); w.write(',');
                w.write(Float.toString(r.getTightClassCohesion())); w.write(',');
                w.write(Float.toString(r.getLooseClassCohesion()));
                w.write('\n');
            }
        }
    }

    private static void writeMethodCsv(File file, List<CKClassResult> results) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("class,method,qualified,loc,wmc,parameters,variables,returns,maxNestedBlocks,loops,comparisons,tryCatch\n");
            for (CKClassResult r : results) {
                Set<CKMethodResult> methods = r.getMethods();
                for (CKMethodResult m : methods) {
                    w.write(escape(r.getClassName())); w.write(',');
                    w.write(escape(m.getMethodName())); w.write(',');
                    w.write(escape(m.getQualifiedMethodName())); w.write(',');
                    w.write(Integer.toString(m.getLoc())); w.write(',');
                    w.write(Integer.toString(m.getWmc())); w.write(',');
                    w.write(Integer.toString(m.getParametersQty())); w.write(',');
                    w.write(Integer.toString(m.getVariablesQty())); w.write(',');
                    w.write(Integer.toString(m.getReturnQty())); w.write(',');
                    w.write(Integer.toString(m.getMaxNestedBlocks())); w.write(',');
                    w.write(Integer.toString(m.getLoopQty())); w.write(',');
                    w.write(Integer.toString(m.getComparisonsQty())); w.write(',');
                    w.write(Integer.toString(m.getTryCatchQty()));
                    w.write('\n');
                }
            }
        }
    }

    // Maintainability Index approximation without Halstead: MI' = max(0, (171 - 0.23*CC - 16.2*ln(LOC)) * 100/171)
    private static void writeMiCsv(File file, List<CKClassResult> results) throws IOException {
        double totalLoc = 0;
        double totalCc = 0; // sum of method WMCs per class == class WMC

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("class,loc,cc,mi_prime\n");
            for (CKClassResult r : results) {
                int loc = Math.max(1, r.getLoc());
                int cc = Math.max(0, r.getWmc());
                double mi = miPrime(loc, cc);
                w.write(escape(r.getClassName())); w.write(',');
                w.write(Integer.toString(loc)); w.write(',');
                w.write(Integer.toString(cc)); w.write(',');
                w.write(String.format(java.util.Locale.ROOT, "%.2f", mi));
                w.write('\n');
                totalLoc += loc;
                totalCc += cc;
            }
            // project-level line
            w.write("_PROJECT_,");
            w.write(Integer.toString((int) totalLoc)); w.write(',');
            w.write(Integer.toString((int) totalCc)); w.write(',');
            w.write(String.format(java.util.Locale.ROOT, "%.2f", miPrime((int) totalLoc, (int) totalCc)));
            w.write('\n');
        }
    }

    private static double miPrime(int loc, int cc) {
        // guard against invalid loc
        double lnLoc = Math.log(Math.max(1, loc));
        double raw = 171 - 0.23 * cc - 16.2 * lnLoc;
        double mi = raw * 100.0 / 171.0;
        if (mi < 0) return 0;
        if (mi > 100) return 100;
        return mi;
    }

    private static String escape(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\"")) {
            return '"' + v + '"';
        }
        return v;
    }
}

