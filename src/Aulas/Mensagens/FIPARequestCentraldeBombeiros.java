package Aulas.Mensagens;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import java.util.StringTokenizer;

public class FIPARequestCentraldeBombeiros extends Agent {

    public double DISTANCIA_MAX;

    protected void setup() {

        DISTANCIA_MAX = (Math.random() * 10);
        System.out.println("Central " + getLocalName() + ": Aguardando alarmes...");
        //Meu agente conversa sob o protocolo FIPA REQUEST
        MessageTemplate protocolo = MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        MessageTemplate performativa = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        MessageTemplate padrao = MessageTemplate.and(protocolo, performativa);

        addBehaviour(new Participante(this, padrao));
    }

    class Participante extends AchieveREResponder {

        public Participante(Agent a, MessageTemplate mt) {
            //Define agente e protocolo de comunica��o
            super(a, mt);
        }

        /* M�todo que aguarda uma mensagem REQUEST, definida com o uso do objeto mt, utilizando no construtor desta classe. 
        O retorno deste m�todo � uma mensagem que � enviada automaticamente para o iniciador. */
        protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {

            System.out.println("Central " + getLocalName() + ": Recebemos uma chamada de " + request.getSender().getName() +
                    " dizendo que observou um inc�ndio");


/*A classe StringTokenizer permite que voc� separe
* ou encontre palavras (tokens) em qualquer formato.*/

            StringTokenizer st = new StringTokenizer(request.getContent());
            String conteudo = st.nextToken(); //pego primeiro token
            if (conteudo.equalsIgnoreCase("fogo")) { //se for fogo
                st.nextToken(); //pulo o segundo 
                int distancia = Integer.parseInt(st.nextToken()); //capturo DIST
                if (distancia < DISTANCIA_MAX) {
                    System.out.println("Central " + getLocalName() + ": Saimos correndo!");

                    ACLMessage agree = request.createReply();
                    agree.setPerformative(ACLMessage.AGREE);
                    return agree;  //envia mensagem AGREEE
                } else {
                    //Fogo est� longe. Envia Mensagem Refuse com o motivo
                    System.out.println("Central " + getLocalName() + ": Fogo est� longe demais. N�o podemos atender a solicita��o.");
                    throw new RefuseException("Fogo est� muito longe");
                }
            } //envia mensagem NOT_UNDERSTOOD
            else {
                throw new NotUnderstoodException("Central de Bombeiros n�o entendeu sua mensagem");
            }

        }

        //Prepara resultado final, caso tenha aceitado
        
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            if (Math.random() > 0.2) {
                System.out.println("Central " + getLocalName() + ": Voltamos de apagar o fogo.");
                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform; //envia mensagem INFORM
            } else {
                System.out.println("Central " + getLocalName() + ": Ficamos sem �gua");
                throw new FailureException("Ficamos sem �gua");
            }

        }
        }
    }
    

