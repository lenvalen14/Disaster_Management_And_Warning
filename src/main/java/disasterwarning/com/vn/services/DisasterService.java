package disasterwarning.com.vn.services;
import disasterwarning.com.vn.models.dtos.DisasterDTO;
import disasterwarning.com.vn.models.entities.Disaster;
import disasterwarning.com.vn.models.entities.DisasterInfo;
import disasterwarning.com.vn.models.entities.DisasterWarning;
import disasterwarning.com.vn.repositories.DisasterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DisasterService implements IDisasterService{

    @Autowired
    private DisasterRepo disasterRepo;

    @Autowired
    private Mapper mapper;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public DisasterDTO createDisaster(DisasterDTO disasterDTO, MultipartFile image) throws IOException {
        Disaster newDisaster = mapper.convertToEntity(disasterDTO, Disaster.class);

        if(!image.isEmpty()){
            String imageURL = fileUploadService.uploadImage(image);
            newDisaster.setImageUrl(imageURL);
        }

        disasterRepo.save(newDisaster);
        return mapper.convertToEntity(newDisaster, DisasterDTO.class);
    }

    @Override
    public DisasterDTO updateDisaster(int id,DisasterDTO disasterDTO, MultipartFile image) throws IOException{
        Disaster disaster = mapper.convertToEntity(disasterDTO, Disaster.class);

        Disaster existingDisaster = disasterRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("Disaster not found"));

        if(!image.isEmpty()){
            String imageURL = fileUploadService.uploadImage(image);
            existingDisaster.setImageUrl(imageURL);
        }

        existingDisaster.setDescription(disaster.getDescription());
        existingDisaster.setDisasterName(disaster.getDisasterName());

        disasterRepo.save(existingDisaster);
        return mapper.convertToEntity(existingDisaster, DisasterDTO.class);
    }

    @Override
    public DisasterDTO findDisasterById(int id){
        Disaster disaster = disasterRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("Disaster not found"));
        return mapper.convertToEntity(disaster, DisasterDTO.class);
    }

    @Override
    public List<DisasterDTO> findAllDisaster(){
        List<Disaster> disasterList = disasterRepo.findAll();
        if(disasterList.isEmpty()){
            throw new RuntimeException("Disaster list is empty");
        }
        return mapper.convertToEntityList(disasterList, DisasterDTO.class);
    }

    @Override
    public boolean deleteDisaster(int id){
        Disaster disaster = disasterRepo
                .findById(id).orElseThrow(()-> new RuntimeException("Disaster not found"));

        List<DisasterInfo> disasterInfos = disasterRepo.getDisasterInfos();
        if(!disasterInfos.isEmpty()){
            throw new RuntimeException("Disaster info is not empty");
        }

        List<DisasterWarning> disasterWarnings = disasterRepo.getDisasterWarnings();
        if(!disasterWarnings.isEmpty()){
            throw new RuntimeException("Disaster warnings is not empty");
        }

        disasterRepo.delete(disaster);
        return true;
    }
}
