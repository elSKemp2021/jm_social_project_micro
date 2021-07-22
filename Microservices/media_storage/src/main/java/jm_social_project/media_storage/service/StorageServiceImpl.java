package jm_social_project.media_storage.service;


import jm_social_project.media_storage.exception.StorageException;
import jm_social_project.media_storage.model.Account;
import jm_social_project.media_storage.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${mediastorage.path}")
    private String URI;

    @Autowired
    private Environment env;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProfilePhotoService profilePhotoService;

    @Autowired
    AccountRepository accountRepository;


    public void store(MultipartFile file, Long id) {

        String contentType = file.getContentType();

        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        if (contentType.startsWith("video")) {
            storeVideo(file, id);

        } else if (contentType.startsWith("image")) {
            storePhoto(file, id);

        }

    }

    private void storeVideo(MultipartFile file,
                            Long id) throws StorageException {

        Path rootLocation = Path.of(URI + env.getProperty("mediastorage.video") + id);

        Path destinationFile = rootLocation
                .resolve(Paths.get(file.getOriginalFilename()));

        try {
            storeFileToFilesystem(rootLocation,
                    destinationFile, file);

        } catch (IOException e) {
            throw new StorageException("Failed to store video.", e);
        }

        accountRepository.save(id, destinationFile);

    }

    private void storePhoto(MultipartFile file,
                            Long id) throws StorageException {

        String NamePattern = "photo-%d-%d.%s";


        Path rootLocation = Path.of(URI + env.getProperty("mediastorage.photo") + id);

        Account account = accountService.findByUserEmail(principal.getName());

        int photoNumber = profilePhotoService
                .findAllByProfileId(account.getProfile()).size() + 1;

//        long profileId = account.getProfile().getId();

        String fileExtension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        String fileName = String.format(NamePattern, id,
                photoNumber, fileExtension);

        Path destinationFile = rootLocation.resolve(Paths.get(fileName));

        ProfilePhoto photo = new ProfilePhoto(destinationFile.toString(),
                account.getProfile());


        try {
            storeFileToFilesystem(rootLocation,
                    destinationFile, file);

        } catch (IOException e) {
            throw new StorageException("Failed to store photo.", e);
        }

        profilePhotoService.add(photo);

        accountRepository.save(id, destinationFile);

    }

    private void storeFileToFilesystem(Path rootLocation,
                                       Path destinationFile,
                                       MultipartFile file) throws IOException {
        Files.createDirectories(rootLocation);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile);
        }
    }
}
