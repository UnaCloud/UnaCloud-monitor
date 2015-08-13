/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package physicalmachine;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.hyperic.sigar.Sigar;

/**
 * Defines a new sigar wrapper
 * @author Clouder
 */
public abstract class SigarWrapper {
    static Sigar sigar=new Sigar();
    static NumberFormat format = new DecimalFormat("#0.000");
}
