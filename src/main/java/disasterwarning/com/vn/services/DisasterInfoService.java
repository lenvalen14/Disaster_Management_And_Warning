package disasterwarning.com.vn.services;

import disasterwarning.com.vn.models.dtos.DisasterInfoDTO;
import disasterwarning.com.vn.models.entities.Disaster;
import disasterwarning.com.vn.models.entities.DisasterInfo;
import disasterwarning.com.vn.models.entities.Image;
import disasterwarning.com.vn.repositories.DisasterInfoRepo;
import disasterwarning.com.vn.repositories.DisasterRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DisasterInfoService implements IDisasterInfoService {
    private final DisasterInfoRepo disasterInfoRepo;
    private final FileUploadService fileUploadService;
    private final DisasterRepo disasterRepo;
    private final Mapper mapper;

    public DisasterInfoService(DisasterInfoRepo disasterInfoRepo, Mapper mapper,FileUploadService fileUploadService,DisasterRepo disasterRepo) {
        this.disasterInfoRepo = disasterInfoRepo;
        this.fileUploadService = fileUploadService;
        this.disasterRepo = disasterRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public DisasterInfoDTO createDisasterInfo(DisasterInfoDTO disasterInfoDTO, List<MultipartFile> images) {

        // check Disaster khi tạo DisasterInfo
        int disasterId = disasterInfoDTO.getDisaster().getDisasterId();

        Disaster disaster = disasterRepo.findById(disasterId)
                .orElseThrow(() -> new RuntimeException("Disaster không tồn tại với ID: " + disasterId));

        // Convert DTO to Entity
        DisasterInfo disasterInfo = mapper.convertToEntity(disasterInfoDTO, DisasterInfo.class);


        // Save disasterInfo
        DisasterInfo savedDisasterInfo = disasterInfoRepo.save(disasterInfo);

        // upload ảnh và lưu vào cơ sở dữ liệu
        if (images != null && !images.isEmpty()) {
            List<Image> imageEntities = new ArrayList<>();

            for (MultipartFile imageFile : images) {
                try {
                    // Upload từng file ảnh lên Cloudinary và lấy URL
                    String imageUrl = fileUploadService.uploadImage(imageFile);

                    //Image và  disasterInfo
                    Image imageEntity = new Image();
                    imageEntity.setImageUrl(imageUrl);
                    imageEntity.setDisasterInfo(savedDisasterInfo);  //  Linking Image và DisasterInfo

                    // add image to images
                    imageEntities.add(imageEntity);
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi khi upload ảnh: " + imageFile.getOriginalFilename(), e);
                }
            }

            // Liên kết các ảnh vào disasterInfo và lưu lại vào cơ sở dữ liệu
            savedDisasterInfo.setImages(imageEntities);
            disasterInfoRepo.save(savedDisasterInfo);
        }

        // Convert back to DTO and return
        return mapper.convertToDto(savedDisasterInfo, DisasterInfoDTO.class);
    }

    @Override
    public DisasterInfoDTO findDisasterInfoById(int id) {
        DisasterInfo disasterInfo = disasterInfoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Disaster Info does not exist"));
        return mapper.convertToDto(disasterInfo, DisasterInfoDTO.class);
    }

    @Override
    public List<DisasterInfoDTO> findAllDisasterInfos() {
        List<DisasterInfo> disasterInfos = disasterInfoRepo.findAll();
        return mapper.convertToDtoList(disasterInfos, DisasterInfoDTO.class);
    }

    @Override
    public DisasterInfoDTO updateDisasterInfo(int id, DisasterInfoDTO disasterInfoDTO) {
        // Fetch existing disasterInfỏ from repository
        Optional<DisasterInfo> disasterInfoOpt = disasterInfoRepo.findById(id);

        // Check if the disaster info exists, otherwise throw an error
        if (disasterInfoOpt.isEmpty()) {
            throw new RuntimeException("DisasterInfo with ID " + id + " does not exist");
        }

        // Get the existing entity
        DisasterInfo existingDisasterInfo = disasterInfoOpt.get();

        // Map fields from DTO to the existing entity
        DisasterInfo updatedDisasterInfo = mapper.convertToEntity(disasterInfoDTO, DisasterInfo.class);

        // Ensure the ID remains the same, as we're updating an existing record
        updatedDisasterInfo.setDisasterInfoId(existingDisasterInfo.getDisasterInfoId());


        updatedDisasterInfo.setDisaster(existingDisasterInfo.getDisaster());  // Keep the associated disaster
        // TODO: save image

        // Save the updated disaster info
        DisasterInfo savedDisasterInfo = disasterInfoRepo.save(updatedDisasterInfo);

        // Convert the updated entity back to DTO and return it
        return mapper.convertToDto(savedDisasterInfo, DisasterInfoDTO.class);
    }

    @Override
    public boolean deleteDisasterInfo(int id) {
        // Check if the disaster info exists
        if (disasterInfoRepo.existsById(id)) {
            disasterInfoRepo.deleteById(id);
            return true;
        } else {
            throw new RuntimeException("DisasterInfo not found with id " + id);
        }
    }

}
