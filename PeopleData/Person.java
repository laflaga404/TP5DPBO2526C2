
public class Person {
    private String id;
    private String nama;
    private int tahunLahir;
    private String pangkat;
    private String email;
    private String status;

    // Constructor
    public Person(String id, String nama, int tahunLahir, String pangkat, String email, String status) {
        this.id = id;
        this.nama = nama;
        this.tahunLahir = tahunLahir;
        this.pangkat = pangkat;
        this.email = email;
        this.status = status;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getTahunLahir() {
        return tahunLahir;
    }

    public String getPangkat() {
        return pangkat;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTahunLahir(int tahunLahir) {
        this.tahunLahir = tahunLahir;
    }

    public void setPangkat(String pangkat) {
        this.pangkat = pangkat;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
