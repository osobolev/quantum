package quantum.qtree.pascal;

import java.math.BigInteger;

public final class Rational {

    public static final Rational ZERO = new Rational(0);
    public static final Rational ONE = new Rational(1);

    private final BigInteger nom;
    private final BigInteger denom;

    public Rational(int i) {
        this(i, 1);
    }
    public Rational(int nom, int denom) {
        this(BigInteger.valueOf(nom), BigInteger.valueOf(denom));
    }

    public Rational(BigInteger nom, BigInteger denom) {
        if (nom.equals(BigInteger.ZERO)) {
            this.nom = nom;
            this.denom = BigInteger.ONE;
        } else {
            BigInteger gcd = nom.gcd(denom);
            this.nom = nom.divide(gcd);
            this.denom = denom.divide(gcd);
        }
    }

    public Rational mul(Rational that) {
        return new Rational(this.nom.multiply(that.nom), this.denom.multiply(that.denom));
    }

    public Rational div(Rational that) {
        return new Rational(this.nom.multiply(that.denom), this.denom.multiply(that.nom));
    }

    public Rational add(Rational that) {
        BigInteger nom = this.nom.multiply(that.denom).add(that.nom.multiply(this.denom));
        BigInteger denom = this.denom.multiply(that.denom);
        return new Rational(nom, denom);
    }

    public Rational sub(Rational that) {
        BigInteger nom = this.nom.multiply(that.denom).subtract(that.nom.multiply(this.denom));
        BigInteger denom = this.denom.multiply(that.denom);
        return new Rational(nom, denom);
    }

    public double toDouble() {
        return nom.doubleValue() / denom.doubleValue();
    }

    public String toString() {
        if (BigInteger.ZERO.equals(nom)) {
            return "0";
        } else {
            return nom + "/" + denom;
        }
    }

    public static BigInteger[] toCommonDenom(Rational... vals) {
        BigInteger denom = BigInteger.ONE; // todo: NOK
        BigInteger gcd = null;
        for (Rational val : vals) {
            denom = denom.multiply(val.denom);
            if (gcd == null) {
                gcd = val.denom;
            } else {
                gcd = gcd.gcd(val.denom);
            }
        }
        if (gcd != null) {
            denom = denom.divide(gcd);
        }
        BigInteger[] result = new BigInteger[vals.length];
        for (int i = 0; i < vals.length; i++) {
            result[i] = vals[i].nom.multiply(denom.divide(vals[i].denom));
        }
        return result;
    }
}
