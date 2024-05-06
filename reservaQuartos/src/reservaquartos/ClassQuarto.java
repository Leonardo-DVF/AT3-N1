package reservaquartos;

public class ClassQuarto{

    private final int maxQtdeQuartos = 20;
    private int nrQuarto;
    private String nomeQuarto, statusQuarto;
    private Boolean quartoDisponivel, quartoEmLimpeza, chaveRecepcao;
    private int qtdeHospedesPorQuarto;
    private String nomeHospedesPorQuarto;
    
    public int getMaxQtdeQuartos() {
        return maxQtdeQuartos;
    }

    public int getNrQuarto() {
        return nrQuarto;
    }

    public void setNrQuarto(int nrQuarto) {
        this.nrQuarto = nrQuarto;
    }

    public String getNomeQuarto() {
        return nomeQuarto;
    }

    public void setNomeQuarto(String nomeQuarto) {
        this.nomeQuarto = nomeQuarto;
    }

    public Boolean getQuartoDisponivel() {
        return quartoDisponivel;
    }

    public void setQuartoDisponivel(Boolean quartoDisponivel) {
        this.quartoDisponivel = quartoDisponivel;
    }

    public Boolean getQuartoEmLimpeza() {
        return quartoEmLimpeza;
    }

    public void setQuartoEmLimpeza(Boolean quartoEmLimpeza) {
        this.quartoEmLimpeza = quartoEmLimpeza;
    }

    public int getQtdeHospedesPorQuarto() {
        return qtdeHospedesPorQuarto;
    }

    public void setQtdeHospedesPorQuarto(int qtdeHospedesPorQuarto) {
        this.qtdeHospedesPorQuarto = qtdeHospedesPorQuarto;
    }

    public String getNomeHospedesPorQuarto() {
        return nomeHospedesPorQuarto;
    }

    public void setNomeHospedesPorQuarto(String nomeHospedesPorQuarto) {
        this.nomeHospedesPorQuarto = nomeHospedesPorQuarto;
    } 

    public Boolean getChaveRecepcao() {
        return chaveRecepcao;
    }

    public void setChaveRecepcao(Boolean chaveRecepcao) {
        this.chaveRecepcao = chaveRecepcao;
    }

    public String getStatusQuarto() {
        return statusQuarto;
    }

    public void setStatusQuarto(String statusQuarto) {
        this.statusQuarto = statusQuarto;
    }
       
}
