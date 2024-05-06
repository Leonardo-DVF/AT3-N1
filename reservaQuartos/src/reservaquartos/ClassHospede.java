package reservaquartos;

public class ClassHospede extends Thread{
    private String nomeHospede;
    private Boolean hospedeAtendido = false;
    private int qtdePessoas;

    public String getNomeHospede() {
        return nomeHospede;
    }

    public void setNomeHospede(String nomeHospede) {
        this.nomeHospede = nomeHospede;
    }

    public Boolean getHospedeAtendido() {
        return hospedeAtendido;
    }

    public void setHospedeAtendido(Boolean hospedeAtendido) {
        this.hospedeAtendido = hospedeAtendido;
    }

    public int getQtdePessoas() {
        return qtdePessoas;
    }

    public void setQtdePessoas(int qtdePessoas) {
        this.qtdePessoas = qtdePessoas;
    }
       
}