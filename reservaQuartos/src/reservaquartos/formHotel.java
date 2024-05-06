package reservaquartos;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;

public final class formHotel extends javax.swing.JFrame {


    //===================================================================================================
    //INÍCIO DO BLOCO
    //===================================================================================================
    
    //MÉTODO PARA A LISTA DE QUARTOS DISPONÍVEIS
    private List<ClassQuarto> listaQuartosDisponiveis = new ArrayList<ClassQuarto>();
    
    public List<ClassQuarto> getListaQuartosDisponiveis() {
        return listaQuartosDisponiveis;
    }

    public void setListaQuartosDisponiveis(List<ClassQuarto> listaQuartosDisponiveis) {
        this.listaQuartosDisponiveis = listaQuartosDisponiveis;
    }
    
    //MÉTODO PARA A LISTA DE HÓSPEDES
    private List<ClassHospede> listaHospedes = new ArrayList<ClassHospede>();
    
    public List<ClassHospede> getListaHospedes() {
        return listaHospedes;
    }

    public synchronized void setListaHospedes(List<ClassHospede> listaHospedes) {
        this.listaHospedes = listaHospedes;
    }

    //MÉTODO PARA A LISTA DE RECLAMAÇÃO
    private List<ClassReclamacao> listaReclamacao = new ArrayList<ClassReclamacao>();

    public List<ClassReclamacao> getListaReclamacao() {
        return listaReclamacao;
    }

    public synchronized void setListaReclamacao(List<ClassReclamacao> listaReclamacao) {
        this.listaReclamacao = listaReclamacao;
    }
    //===================================================================================================
    // FIM DAS VARIÁVEIS DE LISTA
    //===================================================================================================
    
    //===================================================================================================
    // BLOCO DE FUNCIONÁRIOS
    //===================================================================================================
    //MÉTODOS PARA CRIAÇÃO DO HÓSPEDE
    public synchronized void criarHospede(String grupoThread, int qtdeHospede){
        Runnable runHospede =  new Runnable() {
            public void run(){
                try{
                    Thread.sleep(100);
                    checkinHospede(Thread.currentThread().getName());
                }catch(Exception err){
                    err.printStackTrace();
                }
            }
        };
        
        String nameHospede = null;
        
        System.out.println("OK - Hóspedes procurando estadia! ");
        jcb_HospedeSelecao.removeAllItems();
        Thread.currentThread().setName(grupoThread);
        for ( int i=0; i< qtdeHospede; i++){
            Thread t = new Thread(runHospede);
            t.setName("Hospede "+i);
            t.start();
            jcb_HospedeSelecao.addItem("Hospede " + i);
        }
    }  
    
    //MÉTODOS PARA O CHECKIN DOS HÓSPEDES
    public synchronized void checkinHospede(String tName){
        List<ClassHospede> listaHospedesAtual = getListaHospedes();
        //Adiciona o primeiro hóspede
        if(listaHospedesAtual.isEmpty()) {
            adicionarHospede(tName, listaHospedesAtual);
        }else{
            for(int i = 0; i < listaHospedesAtual.size();i++){
                if(!listaHospedesAtual.get(i).getNomeHospede().contains(tName)){
                    adicionarHospede(tName, listaHospedesAtual);
                    break;
                }else{
                    JOptionPane.showMessageDialog(null,"Hóspede Já cadastrado","Atenção",2);
                }
            }
        }
    }

    //MÉTODOS PARA ADICIONAR HÓSPEDE
    public synchronized void adicionarHospede(String tName, List<ClassHospede> listaHospedesAtual){
        try{
            ClassHospede novoHospede = new ClassHospede();
            novoHospede.setNomeHospede(tName);
            novoHospede.setHospedeAtendido(false);
            novoHospede.setQtdePessoas(new Random().nextInt(1,8));
            listaHospedesAtual.add(novoHospede);
            this.setListaHospedes(listaHospedesAtual);
        }catch(Exception e){}
    }
    
    //MÉTODOS PARA O RECEPCIONISTA
    public void criarRecepcionista(String grupoThread, int qtdeRecepcionista){
        String treadName = null;
        Runnable runRecepcionista = new Runnable() {
            private volatile boolean isRunning = true;
            public void run(){
                try{
                    Thread.sleep(1000);
                    realizarAtendimento(Thread.currentThread().getName());
                }catch(Exception err){}
            }
            
            public void kill(){
                isRunning = false;
            }
        };
        
        System.out.println("OK - Recepcionistas prontos para o atendimento! ");
        Thread.currentThread().setName(grupoThread);
        for ( int i=0; i< qtdeRecepcionista; i++){
            treadName = "Recepcionista "+i;
            Thread t = new Thread(runRecepcionista);
            t.setName(treadName);
            t.start();
        }
    }  

    //MÉTODOS PARA REALIZAR O ATENDIMENTO
    public synchronized void realizarAtendimento(String tName){
        List<ClassHospede> listaHospedes = getListaHospedes();   
            for(int i=0; i<listaHospedes.size();i++){
                if(listaHospedes.get(i).getHospedeAtendido() == false){
                    //Arredonda os quartos para um número inteiro
                    double qQuartos = ((double) listaHospedes.get(i).getQtdePessoas()/ 4);
                    int qtdeQuartosNecessarios = (int) Math.ceil(qQuartos);
                    realizarReserva(tName, listaHospedes.get(i).getNomeHospede(), listaHospedes.get(i).getQtdePessoas(), qtdeQuartosNecessarios);
                    listaHospedes.get(i).setHospedeAtendido(true);
                    setListaHospedes(listaHospedes);
                }
            }        
    }
    
    //MÉTODOS PARA REALIZAR A RESERVA
    public synchronized void realizarReserva(String tName, String tNameHospede, int qdePessoas, int qtdeQuartosNecessarios){
        try{
            List<ClassQuarto> listaQuartos = getListaQuartosDisponiveis();
            Boolean realizouReserva = false;
            int qtdeTentativas = 0;
            
                for(int vTentativas = 0; vTentativas < 2; vTentativas++){
                    for(int i = 0; i < listaQuartos.size();i++){
                        if(!realizouReserva){
                            if(listaQuartos.get(i).getQuartoDisponivel()){
                                realizouReserva = alocarQuarto(qdePessoas,i,tNameHospede,qtdeQuartosNecessarios);
                            }
                        }

                        if(realizouReserva) break;
                    }                    
                    if(realizouReserva) break;
                    else if(vTentativas == 1) registraReclamacao(tNameHospede, "Hotel é muito ruim. Não consegui realizar a reserva!");
                }
        }catch(Exception e){
            
        }    
    }

    //MÉTODOS PARA ALOCAR QUARTOS
    public synchronized Boolean alocarQuarto(int qtdePessoas, int i, String tNameHospede, int qtdeQuartosNecessarios){
       Boolean reservou = false;
       List<ClassQuarto> listaQuartos = getListaQuartosDisponiveis();
       if(qtdeQuartosNecessarios>1){
           listaQuartos.get(i).setQtdeHospedesPorQuarto(4);
           listaQuartos.get(i).setNomeHospedesPorQuarto(tNameHospede + "(" + qtdePessoas + ")");
           listaQuartos.get(i).setQuartoDisponivel(false);
           listaQuartos.get(i).setChaveRecepcao(false);
           listaQuartos.get(i).setStatusQuarto("OCUPADO");
           i = i+1;
           listaQuartos.get(i).setQtdeHospedesPorQuarto(qtdePessoas - 4);
           listaQuartos.get(i).setNomeHospedesPorQuarto(tNameHospede + "(" + qtdePessoas + ")");
           listaQuartos.get(i).setQuartoDisponivel(false);
           listaQuartos.get(i).setChaveRecepcao(false);
           listaQuartos.get(i).setStatusQuarto("OCUPADO");
           atualizarModelTable(listaQuartos);
           reservou = true;
       }else{
           listaQuartos.get(i).setQtdeHospedesPorQuarto(qtdePessoas);
           listaQuartos.get(i).setNomeHospedesPorQuarto(tNameHospede + "(" + qtdePessoas + ")");
           listaQuartos.get(i).setQuartoDisponivel(false);
           listaQuartos.get(i).setChaveRecepcao(false);
           listaQuartos.get(i).setStatusQuarto("OCUPADO");
           atualizarModelTable(listaQuartos);
           reservou = true;
       }
       return reservou;
    }

    //MÉTODOS PARA REGISTRAR A RECLAMAÇÃO
    public void registraReclamacao(String tNameHospede, String declaracaoReclamacao){
        List<ClassReclamacao> listaReclamacao = getListaReclamacao();
        ClassReclamacao novaReclamacao = new ClassReclamacao();
        
        novaReclamacao.setNomeHospede(tNameHospede);
        novaReclamacao.setDescricacaoReclamacao(declaracaoReclamacao);
        
        listaReclamacao.add(novaReclamacao);
        atualizarModelTableReclamacao(listaReclamacao);
        
        //Remove quem fez a reclação do combo de simulação de hóspedes
        for(int i=0; i < jcb_HospedeSelecao.getItemCount();i++){
            if(jcb_HospedeSelecao.getItemAt(i).trim().equals(tNameHospede.toString().trim())) jcb_HospedeSelecao.removeItemAt(i);
        }

    }
    
    //MÉTODOS PARA A CAMAREIRA
    public void criarCamareira(String grupoThread, int qtdeCamareira){
        String treadName = null;
        Runnable runCamareira = new Runnable() {
            private volatile boolean isRunning = true;
            @Override
            public void run(){
                try{
                    Thread.sleep(1000);
                    while(isRunning){
                        realizarLimpezaQuarto(Thread.currentThread().getName());
                    }                    
                }catch(InterruptedException err){}
            }
            
            public void kill(){
                isRunning = false;
            }
        };
        
        Thread.currentThread().setName(grupoThread);
        Boolean camareiraJaCriada = false;
        for ( int i=0; i< qtdeCamareira; i++){
            treadName = "Camareira "+i;
            
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
       
            for ( Thread listaThreads : threadSet) 
                if(listaThreads.getName().equals(treadName)) camareiraJaCriada = true;
            
            if(!camareiraJaCriada){
                Thread t = new Thread(runCamareira);
                t.setName(treadName);
                t.start();
                camareiraJaCriada = false;
            }
        }
        System.out.println("OK - Camareiras monitorando os quartos! ");
    }
    
    //MÉTODOS PARA FAZER A LIMPEZA DO QUARTO
    public void realizarLimpezaQuarto(String grupoThread) throws InterruptedException{
        try{
            List<ClassQuarto> listaQuartos;
            String nomeHospede = null;
            Boolean atualizouTabela = false;
            System.out.print(".");
            
            listaQuartos = getListaQuartosDisponiveis();
            
            for(int i=0; i<listaQuartos.size();i++){
                if(listaQuartos.get(i).getStatusQuarto().equals("QUARTO PARA LIMPEZA") ){
                    nomeHospede = listaQuartos.get(i).getNomeHospedesPorQuarto();
                    listaQuartos.get(i).setChaveRecepcao(false);
                    listaQuartos.get(i).setQuartoEmLimpeza(true);    
                    listaQuartos.get(i).setStatusQuarto("EM LIMPEZA");

                    Thread.sleep(3000);
                    atualizarModelTable(listaQuartos);
                    Thread.sleep(3000);

                    listaQuartos.get(i).setQuartoEmLimpeza(false);
                    listaQuartos.get(i).setChaveRecepcao(true);   
                    listaQuartos.get(i).setStatusQuarto("QUARTO LIMPO");
                    atualizarModelTable(listaQuartos);
                }
            } 

            atualizouTabela = false;

            listaQuartos = getListaQuartosDisponiveis();
            for(int i=0; i<listaQuartos.size();i++){
                if(listaQuartos.get(i).getStatusQuarto().equals("QUARTO LIMPO")){
                    Thread.sleep(4000);
                    listaQuartos.get(i).setChaveRecepcao(false);
                    listaQuartos.get(i).setStatusQuarto("CLIENTE VOLTOU!");
                    atualizouTabela = true;
                }
                
                if(listaQuartos.get(i).getStatusQuarto().equals("QUARTO LIBERADO")){
                    Thread.sleep(3000);
                    listaQuartos.get(i).setChaveRecepcao(false);
                    listaQuartos.get(i).setQuartoEmLimpeza(true);
                    listaQuartos.get(i).setStatusQuarto("EM LIMPEZA (VAGO)");
                    atualizarModelTable(listaQuartos);
                    atualizouTabela = true;
                }
                if(listaQuartos.get(i).getStatusQuarto().equals("EM LIMPEZA (VAGO)")){
                    Thread.sleep(3000);
                    listaQuartos.get(i).setQuartoDisponivel(true);
                    listaQuartos.get(i).setChaveRecepcao(true);
                    listaQuartos.get(i).setQuartoEmLimpeza(false); 
                    listaQuartos.get(i).setStatusQuarto("VAGO");
                    atualizouTabela = true;
                }
            }
            if(atualizouTabela) atualizarModelTable(listaQuartos);
            atualizarTotais();
        }catch(Exception e){}
    }

    //===================================================================================================
    // BLOCO CONTROLE TABELAS
    //===================================================================================================

    //Carrega a tabela inicial com modelo de colunas
    public void carregarTabelaPrincipal(){
        ClassQuarto novoQuarto = null;
        
        int qtdQuartosSolicitado = 0;
        try{
            setListaHospedes(new ArrayList<ClassHospede>());
            setListaQuartosDisponiveis(new ArrayList<ClassQuarto>());
            limparModelTable();
            limparModelTableReclamacao();
            
            qtdQuartosSolicitado = Integer.parseInt("" + jtf_qtdQuartos.getValue());
            //Criando os quartos
            for(int i =1; i <= qtdQuartosSolicitado; i++){
                novoQuarto = new ClassQuarto();

                novoQuarto.setNrQuarto(i);
                novoQuarto.setNomeQuarto("Quarto " + (200 + i));
                novoQuarto.setQuartoDisponivel(true);
                novoQuarto.setQuartoEmLimpeza(false);
                novoQuarto.setChaveRecepcao(true);
                novoQuarto.setStatusQuarto("VAGO");
                novoQuarto.setQtdeHospedesPorQuarto(0);
                novoQuarto.setNomeHospedesPorQuarto("");                

                String quartoDisponivel = "Não", quartoEmLimpeza = "Não";

                if(novoQuarto.getQuartoDisponivel() == true){ 
                     quartoDisponivel = "Sim";
                }

                if(novoQuarto.getQuartoEmLimpeza() == true){ 
                     quartoEmLimpeza = "Sim";
                }

                getListaQuartosDisponiveis().add(i-1, novoQuarto);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Quantidade de quartos deverá ser numérica e maior que zero (0)");
        }
    }

    //Limpar o modelo da tabela
    public void limparModelTable(){
        String colunasReservaHotel[] = {"Nr Quarto","Nome Quarto","Disponivel","Quarto em Limpeza","Chave na Recepção?","Status","Qde Hospedes","Nome Hospedes"};
        tQuartosHotel.setModel(new DefaultTableModel(
            new Object [][] {},
            colunasReservaHotel
        ));
    }
    
    //Limpar o modelo da tabela reclamação
    public void limparModelTableReclamacao(){
        String colunasReclamacao[] = {"Nome Hospede","Reclamação"};
        tReclamacoes.setModel(new DefaultTableModel(
            new Object [][] {},
            colunasReclamacao
        ));
    }
    
    //Atualizar o modelo da tabela
    public synchronized void atualizarModelTable(List<ClassQuarto> listaQuartos){
        limparModelTable();
            
        DefaultTableModel model = (DefaultTableModel) tQuartosHotel.getModel();
        for(int i=0;i<listaQuartos.size();i++){
            
            String quartoDisponivel = "Não", quartoEmLimpeza = "Não", quartoChaveRecepcao = "Não", statusQuarto = "OCUPADO";

            if(listaQuartos.get(i).getQuartoDisponivel() == true){ 
                 quartoDisponivel = "Sim";
            }

            if(listaQuartos.get(i).getQuartoEmLimpeza() == true){ 
                 quartoEmLimpeza = "Sim";
            }
            
            if(listaQuartos.get(i).getChaveRecepcao() == true){ 
                 quartoChaveRecepcao = "Sim";
            }
            
            model.addRow(new Object[]
                 {
                     listaQuartos.get(i).getNrQuarto(),
                     listaQuartos.get(i).getNomeQuarto(),
                     quartoDisponivel,
                     quartoEmLimpeza,
                     quartoChaveRecepcao,
                     listaQuartos.get(i).getStatusQuarto(),
                     listaQuartos.get(i).getQtdeHospedesPorQuarto(),
                     listaQuartos.get(i).getNomeHospedesPorQuarto()
                 }
            );
        }
    }
    
    //Atualizar o modelo da tabela
    public synchronized void atualizarModelTableReclamacao(List<ClassReclamacao> listaReclamacao){
        limparModelTableReclamacao();
        
        DefaultTableModel model = (DefaultTableModel) tReclamacoes.getModel();
        for(int i=0;i<listaReclamacao.size();i++){
            model.addRow(new Object[]
                        {
                            listaReclamacao.get(i).getNomeHospede(),
                            listaReclamacao.get(i).getDescricacaoReclamacao()
                        }
            );
        }
    }
        
    //Atualizar passeioHospede
    public synchronized void atualizarPasseioHospede(){
        List<ClassQuarto> listaAtualizadaPasseio = getListaQuartosDisponiveis();
        
        for(int i=0; i < listaAtualizadaPasseio.size();i++){
            if(listaAtualizadaPasseio.get(i).getNomeHospedesPorQuarto().contains("" + jcb_HospedeSelecao.getSelectedItem())){
                listaAtualizadaPasseio.get(i).setChaveRecepcao(true);
                listaAtualizadaPasseio.get(i).setStatusQuarto("QUARTO PARA LIMPEZA");
                atualizarModelTable(listaAtualizadaPasseio);
            }            
        }        
    }
    
    //Atualizar quartos
    public synchronized void liberarQuarto(){
        List<ClassQuarto> listaAtualizadaQuartos = getListaQuartosDisponiveis();
        
        for(int i=0; i < listaAtualizadaQuartos.size();i++){
            if(listaAtualizadaQuartos.get(i).getNomeHospedesPorQuarto().contains("" + jcb_HospedeSelecao.getSelectedItem())){
                listaAtualizadaQuartos.get(i).setQuartoDisponivel(false);
                listaAtualizadaQuartos.get(i).setQtdeHospedesPorQuarto(0);
                listaAtualizadaQuartos.get(i).setNomeHospedesPorQuarto("");
                listaAtualizadaQuartos.get(i).setChaveRecepcao(true);
                listaAtualizadaQuartos.get(i).setStatusQuarto("QUARTO LIBERADO");
                atualizarModelTable(listaAtualizadaQuartos);
            }            
        }        
    }
    
    public void resetVariaveis(){
        setListaReclamacao(new ArrayList<ClassReclamacao>());
        jcb_HospedeSelecao.removeAllItems();
        limparModelTableReclamacao();
    }

    //===================================================================================================
    // BLOCO LOAD
    //===================================================================================================
    //Carrega as Threds de funcinoários do hotel
    public synchronized void carregarFuncionarios(){
        try{
            //Cria as camareiras
            try{
                criarCamareira("ThreadHotel",10);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Quantidade de recepcionistas com problemas");
            }
            
            //Cria os recepcionistas
            try{
                criarRecepcionista("ThreadHotel",5);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Quantidade de recepcionistas com problemas");
            }

            //Cria os hospedes e tentar realizar a reserva na classe de hóspede, utilizando os métodos dessa classe
            int qtdHospedessSolicitado = 0;
            try{
                qtdHospedessSolicitado = Integer.parseInt("" + jtf_qtdHospedes.getValue());
                criarHospede("ThreadHotel",qtdHospedessSolicitado);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Quantidade de hóspedes deverá ser numérica e maior que zero (0)");
            }
            
            imprimirListaHospedes();
           
            listarThreads();
        }catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
    
    //Atualizar os totais do formulário
    public void setTotaisTable(int qtdeHospedes,int qtdeRecepcionistas,int qtdeCamareiras){
        jlb_totalHospedesTable.setText("" + qtdeHospedes);
        jlb_totalRecepcionistasTable.setText("" + qtdeRecepcionistas);
        jlb_totalCamareiras.setText("" + qtdeCamareiras);
        atualizarTotais();
    }

    public synchronized void atualizarTotais(){
        DefaultTableModel modelTotal = (DefaultTableModel) tQuartosHotel.getModel();
        int countQuartoDisponivel = 0;
        for(int i=0;i<modelTotal.getRowCount();i++){
            if(modelTotal.getValueAt(i, 2).equals("Sim")) countQuartoDisponivel = countQuartoDisponivel + 1;
        }
        jlb_totalRecepcionistasTable1.setText("" + countQuartoDisponivel);
    }
    
    //===================================================================================================
    // BLOCO PRINCIPAL
    //===================================================================================================
    
    public formHotel() {
        initComponents();
        
        //Limpar as tabelas da aplicação
        limparModelTable();
        limparModelTableReclamacao();        
    }
    
    

    //===================================================================================================
    // BLOCO DE LOG
    //===================================================================================================
    
    //Lista de hóspedes
    public void imprimirListaHospedes(){
        List<ClassHospede> listaH = getListaHospedes();
                
        if(listaH.isEmpty()){
            //System.out.print("Lista vazia!");
        }else{
            for(int i = 0; i < listaH.size();i++){
                System.out.println("Hóspede: " + listaHospedes.get(i).getNomeHospede() + ": Atendido: " + listaHospedes.get(i).getHospedeAtendido());
            }
        } 
    }
    
    //Lista todas as threads
    public void listarThreads(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        
        int countHospedes = 0, countRecepcionistas = 0, countCamareiras = 0;
        
        for ( Thread t : threadSet){
            if ( t.getThreadGroup() == Thread.currentThread().getThreadGroup()){
                if(t.getName().contains("Hospede ")) countHospedes++;
                if(t.getName().contains("Recepcionista ")) countRecepcionistas++;
                if(t.getName().contains("Camareira ")) countCamareiras++;
            }
        }
        
        setTotaisTable(countHospedes,countRecepcionistas,countCamareiras);
    }

    //Carregamento das Threads
    public void carregarThreads(String threadGroup){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        DefaultTableModel model = (DefaultTableModel) jtb_listaThreads.getModel();

        while(model.getRowCount() > 0){
            for(int i = 0; i < model.getRowCount();i++) {
                model.removeRow(i);
            }
        }
    
        Set<Thread> setThreads = Thread.getAllStackTraces().keySet();
        
        for ( Thread t : setThreads){
            if(t.getName().contains("Camareira") || t.getName().contains("Hospede") || t.getName().contains("Recepcionista"))    
                model.addRow(new Object[]{t.getId(),t.getName(),t.getState()});
        }
    }
    
    //===================================================================================================
    //FINAL DO BLOCO
    //===================================================================================================

 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tQuartosHotel = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tReclamacoes = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtf_qtdQuartos = new javax.swing.JSpinner();
        jtf_qtdHospedes = new javax.swing.JSpinner();
        jbt_start = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jlb_hospede = new javax.swing.JLabel();
        jlb_totalHospedesTable = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jlb_recepcionista = new javax.swing.JLabel();
        jlb_totalRecepcionistasTable = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jlb_quartos = new javax.swing.JLabel();
        jlb_totalRecepcionistasTable1 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jlb_camareiras = new javax.swing.JLabel();
        jlb_totalCamareiras = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jcb_HospedeSelecao = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtb_listaThreads = new javax.swing.JTable();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Reserva Hotel");
        setResizable(false);

        tQuartosHotel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tQuartosHotel.setEditingColumn(0);
        tQuartosHotel.setEditingRow(0);
        tQuartosHotel.setFocusable(false);
        tQuartosHotel.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tQuartosHotel.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tQuartosHotel.setShowGrid(true);
        jScrollPane1.setViewportView(tQuartosHotel);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RECLAMAÇÕES", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        tReclamacoes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        tReclamacoes.setFocusable(false);
        jScrollPane2.setViewportView(tReclamacoes);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 792, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("QUARTOS");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("HÓSPEDES");

        jtf_qtdQuartos.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jtf_qtdQuartos.setModel(new javax.swing.SpinnerNumberModel(10, null, null, 1));
        jtf_qtdQuartos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtf_qtdQuartos.setFocusable(false);
        jtf_qtdQuartos.setOpaque(true);
        jtf_qtdQuartos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtf_qtdQuartosKeyPressed(evt);
            }
        });

        jtf_qtdHospedes.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jtf_qtdHospedes.setModel(new javax.swing.SpinnerNumberModel(10, null, null, 1));
        jtf_qtdHospedes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtf_qtdHospedes.setOpaque(true);

        jbt_start.setBackground(new java.awt.Color(197, 219, 247));
        jbt_start.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jbt_start.setText("Iniciar");
        jbt_start.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbt_startMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtf_qtdQuartos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtf_qtdHospedes, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jbt_start, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtf_qtdQuartos, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtf_qtdHospedes, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbt_start, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlb_hospede.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_hospede.setText("HÓSPEDES");

        jlb_totalHospedesTable.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jlb_totalHospedesTable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_totalHospedesTable.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlb_hospede, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlb_totalHospedesTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jlb_hospede)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlb_totalHospedesTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlb_recepcionista.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_recepcionista.setText("RECEPCIONISTAS");

        jlb_totalRecepcionistasTable.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jlb_totalRecepcionistasTable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_totalRecepcionistasTable.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlb_recepcionista, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlb_totalRecepcionistasTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jlb_recepcionista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlb_totalRecepcionistasTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlb_quartos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_quartos.setText("QUARTOS DISPONÍVEIS");

        jlb_totalRecepcionistasTable1.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jlb_totalRecepcionistasTable1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_totalRecepcionistasTable1.setText("0");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlb_quartos, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlb_totalRecepcionistasTable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jlb_quartos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlb_totalRecepcionistasTable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jlb_camareiras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_camareiras.setText("CAMAREIRAS");

        jlb_totalCamareiras.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jlb_totalCamareiras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlb_totalCamareiras.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlb_camareiras, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlb_totalCamareiras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jlb_camareiras)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlb_totalCamareiras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("SIMULAR AÇÃO DO HÓSPEDE");

        jcb_HospedeSelecao.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jButton2.setText("Ir passear!");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton3.setText("Liberar Reserva!");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jcb_HospedeSelecao, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcb_HospedeSelecao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "THREADS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jtb_listaThreads.setAutoCreateRowSorter(true);
        jtb_listaThreads.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Número Thread", "Nome Thread", "Status Thread"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtb_listaThreads.setFocusable(false);
        jScrollPane3.setViewportView(jtb_listaThreads);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbt_startMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbt_startMouseClicked
        resetVariaveis();
        carregarTabelaPrincipal();
        carregarFuncionarios();
        carregarThreads("ThreadHotel");
    }//GEN-LAST:event_jbt_startMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        atualizarPasseioHospede();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        liberarQuarto();
    }//GEN-LAST:event_jButton3MouseClicked

    private void jtf_qtdQuartosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtf_qtdQuartosKeyPressed

    }//GEN-LAST:event_jtf_qtdQuartosKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws ParseException {

        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(formHotel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new formHotel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton jbt_start;
    private javax.swing.JComboBox<String> jcb_HospedeSelecao;
    private javax.swing.JLabel jlb_camareiras;
    private javax.swing.JLabel jlb_hospede;
    private javax.swing.JLabel jlb_quartos;
    private javax.swing.JLabel jlb_recepcionista;
    private javax.swing.JLabel jlb_totalCamareiras;
    private javax.swing.JLabel jlb_totalHospedesTable;
    private javax.swing.JLabel jlb_totalRecepcionistasTable;
    private javax.swing.JLabel jlb_totalRecepcionistasTable1;
    private javax.swing.JTable jtb_listaThreads;
    private javax.swing.JSpinner jtf_qtdHospedes;
    private javax.swing.JSpinner jtf_qtdQuartos;
    private javax.swing.JTable tQuartosHotel;
    private javax.swing.JTable tReclamacoes;
    // End of variables declaration//GEN-END:variables

}
