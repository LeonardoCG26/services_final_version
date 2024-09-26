package com.example.hojadeservicio;

public class DatosPDF {
    String Fsoli;
    String Fate;
    String Hllega;
    String facu;
    String ediArea;
    String encarg;
    String tele;
    String proble;
    String Solu;
    String Hsali;

    public DatosPDF(String fsoli, String fate, String hllega, String facu, String ediArea, String encarg, String tele, String proble, String solu, String hsali) {
        Fsoli = fsoli;
        Fate = fate;
        Hllega = hllega;
        this.facu = facu;
        this.ediArea = ediArea;
        this.encarg = encarg;
        this.tele = tele;
        this.proble = proble;
        Solu = solu;
        Hsali = hsali;
    }

    public String getFsoli() {
        return Fsoli;
    }

    public void setFsoli(String fsoli) {
        Fsoli = fsoli;
    }

    public String getFate() {
        return Fate;
    }

    public void setFate(String fate) {
        Fate = fate;
    }

    public String getHllega() {
        return Hllega;
    }

    public void setHllega(String hllega) {
        Hllega = hllega;
    }

    public String getFacu() {
        return facu;
    }

    public void setFacu(String facu) {
        this.facu = facu;
    }

    public String getEdiArea() {
        return ediArea;
    }

    public void setEdiArea(String ediArea) {
        this.ediArea = ediArea;
    }

    public String getEncarg() {
        return encarg;
    }

    public void setEncarg(String encarg) {
        this.encarg = encarg;
    }

    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }

    public String getProble() {
        return proble;
    }

    public void setProble(String proble) {
        this.proble = proble;
    }

    public String getSolu() {
        return Solu;
    }

    public void setSolu(String solu) {
        Solu = solu;
    }

    public String getHsali() {
        return Hsali;
    }

    public void setHsali(String hsali) {
        Hsali = hsali;
    }
}
