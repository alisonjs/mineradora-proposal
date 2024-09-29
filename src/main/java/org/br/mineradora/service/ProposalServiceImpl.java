package org.br.mineradora.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.br.mineradora.dto.ProposalDTO;
import org.br.mineradora.dto.ProposalDetailsDTO;
import org.br.mineradora.entity.ProposalEntity;
import org.br.mineradora.message.KafkaEvent;
import org.br.mineradora.repository.ProposalRepository;
import org.modelmapper.ModelMapper;

@Slf4j
@ApplicationScoped
public class ProposalServiceImpl implements ProposalService{

    @Inject
    ProposalRepository proposalRepository;

    @Inject
    KafkaEvent kafkaMessage;

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public ProposalDetailsDTO findFullProposal(long id) {
        ProposalEntity proposalEntity = proposalRepository.findById(id);
        if(proposalEntity == null) {
            throw new NotFoundException("Not found Proposal with id " + id);
        }
        return modelMapper.map(proposalEntity, ProposalDetailsDTO.class);
    }

    @Transactional
    @Override
    public void createNewProposal(ProposalDetailsDTO proposalDetailsDTO) {
        ProposalDTO proposal = buildAndSaveNewProposal(proposalDetailsDTO);
        kafkaMessage.sendNewKafkaEvent(proposal);
    }

    @Transactional
    @Override
    public void removeProposal(long id) {
        if(!proposalRepository.deleteById(id)){
            throw new NotFoundException("Not found Proposal with id " + id);
        }
    }

    private ProposalDTO buildAndSaveNewProposal(ProposalDetailsDTO proposalDetailsDTO) {

        try {
            ProposalEntity proposal = modelMapper.map(proposalDetailsDTO, ProposalEntity.class);
            proposalRepository.persist(proposal);
            return modelMapper.map(proposalRepository.findByCustomer(proposal.getCustomer()), ProposalDTO.class);
        } catch (Exception e) {
            log.error("Error during save new proposal {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
