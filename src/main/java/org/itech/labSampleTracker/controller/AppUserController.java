/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 */
package org.itech.labSampleTracker.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.itech.labSampleTracker.dto.ProfileDTO;
import org.itech.labSampleTracker.dto.UserDTO;
import org.itech.labSampleTracker.dto.UserOrgDTO;
import org.itech.labSampleTracker.entities.AppUser;
import org.itech.labSampleTracker.entities.AppUserHasCircuit;
import org.itech.labSampleTracker.entities.AppUserHasDistrict;
import org.itech.labSampleTracker.entities.AppUserHasLab;
import org.itech.labSampleTracker.entities.AppUserHasRegion;
import org.itech.labSampleTracker.entities.AppUserHasSite;
import org.itech.labSampleTracker.enums.UserLevel;
import org.itech.labSampleTracker.enums.UserRole;
import org.itech.labSampleTracker.enums.UserType;
import org.itech.labSampleTracker.exception.OperationFailedException;
import org.itech.labSampleTracker.exception.ResourceNotFoundException;
import org.itech.labSampleTracker.service.AppUserService;
import org.itech.labSampleTracker.service.CircuitService;
import org.itech.labSampleTracker.service.DistrictService;
import org.itech.labSampleTracker.service.LabService;
import org.itech.labSampleTracker.service.RegionService;
import org.itech.labSampleTracker.service.SiteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>AppUserController</h2>
 */
@Controller
@RequestMapping("/appuser")
public class AppUserController extends BaseController {

	@Autowired
	private AppUserService appuserService;

	@Autowired
	private RegionService regionService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private LabService labService;
	
	@Autowired
	private CircuitService circuitService;

	private AppUser appUser;

	@PostMapping(value = "/new")
	public String createAppUser(@Valid UserDTO userDTO, BindingResult result, Model model) {
		model.addAttribute("userTypes", UserType.values());
		model.addAttribute("userRoles", UserRole.values());
		model.addAttribute("userLevels", UserLevel.values());
		if (result.hasErrors()) {
			model.addAttribute("message_error", result.getAllErrors());
			return "user/new";
		}
		String login = userDTO.getLogin();
		String contact = userDTO.getPhoneContact();

		AppUser user = appuserService.findUserByLogin(login);
		if (user != null) {
			model.addAttribute("message_error", "Ce login est dejà utilisé");
			return "user/new";
		}
		user = appuserService.findUserByPhoneContact(contact);
		if (user != null) {
			model.addAttribute("message_error", "Ce contact est dejà utilisé");
			return "user/new";
		}
		String password = userDTO.getPassword();
		String repassword = userDTO.getRepassword();
		if (!password.equals(repassword)) {
			model.addAttribute("message_error", "Mots de passe non concordants");
			return "user/new";
		}
		AppUser u = new AppUser();
		u.setPassword(password);
		u.setFirstName(userDTO.getFirstName());
		u.setLastName(userDTO.getLastName());
		u.setPhoneContact(contact);
		u.setLogin(login);
		u.setRole(userDTO.getRole().getRole());
		u.setUserType(userDTO.getUserType().getType());
		u.setUserLevel(userDTO.getUserLevel().getLevel());
		u.setPasswordExpireAt(userDTO.getPasswordExpireAt());
		u.setCreatedAt(new java.util.Date());
		u.setIsActive(userDTO.isActive());
		u.setIsLocked(userDTO.isLocked());
		try {
			u = appuserService.create(u, true);

		} catch (Exception e) {
			model.addAttribute("message_error", e.getMessage());
		}
		model.addAttribute("message_success", "Ajout effectué avec succès");

		model.addAttribute("userDTO", new UserDTO());
		return "user/new";
	}

	@GetMapping(value = "/new")
	public String newAppUser(Model model) {
		model.addAttribute("userDTO", new UserDTO());
		model.addAttribute("userTypes", UserType.values());
		model.addAttribute("userRoles", UserRole.values());
		model.addAttribute("userLevels", UserLevel.values());
		return "user/new";
	}

	@GetMapping(value = "")
	public String getAllAppUser(Model model) {
		List<AppUser> appuserList = appuserService.getAll();

		model.addAttribute("users", appuserList);

		return "user/index";
	}

	@GetMapping(value = "/update/{id}")
	public String getOneAppUser(@PathVariable("id") Integer id, Model model) {
		appUser = new AppUser();
		UserDTO userDTO = new UserDTO();
		model.addAttribute("userTypes", UserType.values());
		model.addAttribute("userRoles", UserRole.values());
		model.addAttribute("userLevels", UserLevel.values());
		try {
			appUser = accountService.getOne(id);
			if (ObjectUtils.isEmpty(appUser)) {
				throw new ResourceNotFoundException("Impossible de restituer les données de l'utilisateur");
			}
		} catch (Exception ex) {
			model.addAttribute("message_error", ex.getMessage());
			return "user/edit";
		}
		BeanUtils.copyProperties(appUser, userDTO);
		userDTO.setRole(UserRole.get(appUser.getRole()));
		userDTO.setUserType(UserType.get(appUser.getUserType()));
		userDTO.setUserLevel(UserLevel.get(appUser.getUserLevel()));
		userDTO.setRepassword(null);
		model.addAttribute("userDTO", userDTO);
		return "user/edit";
	}

	@PostMapping(value = "/update/{id}")
	public String updateAppUser(@PathVariable("id") Integer id, Model model, UserDTO userDTO) {
		model.addAttribute("userTypes", UserType.values());
		model.addAttribute("userRoles", UserRole.values());
		model.addAttribute("userLevels", UserLevel.values());
		try {
			AppUser updatedUser = appuserService.findUserById(id).orElse(null);
			if (ObjectUtils.isEmpty(updatedUser)) {
				model.addAttribute("message_error", "Utilisateur non trouvé");
				return "user/edit";
			}
			appUser = new AppUser();
			AppUser u = appuserService.findUserByLogin(userDTO.getLogin());
			if (u != null && !userDTO.getLogin().equalsIgnoreCase(u.getLogin())) {
				model.addAttribute("message_error", "Ce login est dejà utilisé");
				return "user/edit";
			}

			u = appuserService.findUserByPhoneContact(userDTO.getPhoneContact());
			if (u != null && !userDTO.getPhoneContact().equalsIgnoreCase(u.getPhoneContact())) {
				model.addAttribute("message_error", "Ce contact est dejà utilisé");
				return "user/edit";
			}
			boolean passwordMustBeCrypt = false;
			updatedUser.setIsActive(userDTO.isActive());
			updatedUser.setFirstName(userDTO.getFirstName());
			updatedUser.setLastName(userDTO.getLastName());
			updatedUser.setIsLocked(userDTO.isLocked());
			if (StringUtils.isNotEmpty(userDTO.getPassword())) { // si le mot de passe est defini lors de la
																	// modification alors on met a jour le mot de passe.
				String password = userDTO.getPassword();
				String repassword = userDTO.getRepassword();
				if (!password.equals(repassword)) {
					throw new RuntimeException("Mots de passe non concordants");
				}
				updatedUser.setPassword(userDTO.getPassword());
				passwordMustBeCrypt = true;

			} else {
				passwordMustBeCrypt = false;
			}
			updatedUser.setPasswordExpireAt(userDTO.getPasswordExpireAt());
			updatedUser.setPhoneContact(userDTO.getPhoneContact());

			updatedUser.setLastUpdatedBy(this.getCurrentUserId());
			updatedUser.setLastUpdatedAt(new java.util.Date());
			updatedUser.setRole(userDTO.getRole().getRole());
			updatedUser.setUserType(userDTO.getUserType().getType());
			updatedUser.setUserLevel(userDTO.getUserLevel().getLevel());
			appUser = appuserService.create(updatedUser, passwordMustBeCrypt);

		} catch (Exception e) {
			model.addAttribute("message_error", e.getMessage());
			return "user/edit";
		}
		model.addAttribute("user", appUser);
		model.addAttribute("message_success", "Modification effectuée avec succès");
		return "user/edit";
	}

	@GetMapping("/profile/edit")
	public String showProfile(Model model) {
		ProfileDTO userDTO = new ProfileDTO();
		model.addAttribute("user", userDTO);
		try {
			accountService.findUserById(getCurrentUserId()).ifPresent(updatedUser -> {
				userDTO.setId(updatedUser.getId());
				BeanUtils.copyProperties(updatedUser, userDTO, "password,repassword,role");
				model.addAttribute("user", userDTO);
			});
		} catch (Exception e) {
			model.addAttribute("message_error", e.getMessage());
			model.addAttribute("user", new ProfileDTO());
		}
		return "user/profile";
	}

	@PostMapping("/profile/edit")
	public String editProfile(Model model, @Valid ProfileDTO user) {
		if (!this.isGlobalRole()) {
			if (!Objects.equals(this.getCurrentUserId(), user.getId())) {
				throw new AccessDeniedException("Impossible de modifier les informations de cet utilisateur");
			}
		}
		appUser = new AppUser();
		try {
			accountService.findUserById(user.getId()).ifPresent(updatedUser -> {
				updatedUser.setPhoneContact(user.getPhoneContact());
				updatedUser.setLastName(user.getLastName());
				updatedUser.setFirstName(user.getFirstName());

				boolean passwordMustBeCrypt = false;
				if (StringUtils.isNotEmpty(user.getPassword())) { // si le mot de passe est defini lors de la
					// modification alors on met a jour le mot de passe.
					String password = user.getPassword();
					String repassword = user.getRepassword();
					if (!password.equals(repassword)) {
						throw new RuntimeException("Mots de passe non concordants");
					}
					updatedUser.setPassword(user.getPassword());
					passwordMustBeCrypt = true;

				} else {
					passwordMustBeCrypt = false;
				}
				updatedUser.setLastUpdatedBy(this.getCurrentUserId());
				updatedUser.setLastUpdatedAt(new java.util.Date());
				appUser = accountService.update(updatedUser, passwordMustBeCrypt);
			});
		} catch (Exception e) {
			model.addAttribute("message_error", e.getMessage());
			model.addAttribute("user", new ProfileDTO());
		}
		model.addAttribute("user", user);
		model.addAttribute("message_success", "Modification effectuée avec succès");
		return "user/profile";
	}

	@GetMapping("/edit_org/{userId}")
	public String editUserOrg(Model model, @PathVariable("userId") Integer userId) {
		appUser = new AppUser();
		model.addAttribute("regions", regionService.getRegionIdAndName());
		model.addAttribute("districts", districtService.getDistrictIdAndNames());
		model.addAttribute("sites", siteService.getSiteIdAndNames());
		model.addAttribute("labs", labService.getLabIdAndNames());
		model.addAttribute("circuits", circuitService.getCircuitIdAndNumber());
		UserOrgDTO userOrg = new UserOrgDTO();
		try {
			appUser = accountService.getOne(userId);
			if (ObjectUtils.isEmpty(appUser)) {
				throw new ResourceNotFoundException("Utilisateur non trouvé");
			}
			if (appUser.getUserLevel().equals("REGION")) {
				List<AppUserHasRegion> appUserHasRegions = appuserService.getUserRegions(userId);
				List<Integer> usersRegions = appUserHasRegions.stream().map(e -> e.getRegionId())
						.collect(Collectors.toList());
				userOrg.setRegions(usersRegions);
			} else if (appUser.getUserLevel().equals("DISTRICT")) {
				List<AppUserHasDistrict> appUserHasDistricts = appuserService.getUserDistricts(userId);
				List<Integer> userDistricts = appUserHasDistricts.stream().map(e -> e.getDistrictId())
						.collect(Collectors.toList());
				userOrg.setDistricts(userDistricts);
			} else if (appUser.getUserLevel().equals("SITE")) {
				List<AppUserHasSite> appUserHasSites = appuserService.getUserSites(userId);
				List<Integer> usersSites = appUserHasSites.stream().map(e -> e.getSiteId())
						.collect(Collectors.toList());
				userOrg.setSites(usersSites);
			} else if (appUser.getUserLevel().equals("LABO")) {
				List<AppUserHasLab> appUserHasLabs = appuserService.getUserLabs(userId);
				List<Integer> usersLabs = appUserHasLabs.stream().map(e -> e.getLabId()).collect(Collectors.toList());
				userOrg.setLabs(usersLabs);
			} else if (appUser.getUserLevel().equals("CIRCUIT")) {
				List<AppUserHasCircuit> appUserHasCircuits = appuserService.getUserCircuits(userId);
				List<Integer> usersCircuits = appUserHasCircuits.stream().map(e -> e.getCircuitId())
						.collect(Collectors.toList());
				userOrg.setCircuits(usersCircuits);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			model.addAttribute("message_error", ex.getMessage());
			return "user/edit_user_org";
		}

		model.addAttribute("user", appUser);
		model.addAttribute("userOrg", userOrg);

		return "user/edit_user_org";
	}

	@PostMapping("/edit_org/{userId}")
	public String updateUserOrg(Model model, @PathVariable Integer userId, UserOrgDTO userOrg) {
		appUser = new AppUser();
		model.addAttribute("regions", regionService.getRegionIdAndName());
		model.addAttribute("districts", districtService.getDistrictIdAndNames());
		model.addAttribute("sites", siteService.getSiteIdAndNames());
		model.addAttribute("labs", labService.getLabIdAndNames());
		model.addAttribute("circuits", circuitService.getCircuitIdAndNumber());
		model.addAttribute("userOrg", userOrg);

		try {
			appUser = accountService.getOne(userId);
			if (ObjectUtils.isEmpty(appUser)) {
				throw new ResourceNotFoundException("Utilisateur non trouvé");
			}
			model.addAttribute("user", appUser);
			if (appUser.getUserLevel().equals("REGION")) {
				appuserService.addRegionsToUser(userId, userOrg.getRegions());
			} else if (appUser.getUserLevel().equals("DISTRICT")) {
				appuserService.addDistrictsToUser(userId, userOrg.getDistricts());
			} else if (appUser.getUserLevel().equals("SITE")) {
				appuserService.addSitesToUser(userId, userOrg.getSites());
			} else if (appUser.getUserLevel().equals("LABO")) {
				appuserService.addLabsToUser(userId, userOrg.getLabs());
			} else if (appUser.getUserLevel().equals("CIRCUIT")) {
				appuserService.addCircuitsToUser(userId, userOrg.getCircuits());
			}
			model.addAttribute("message_success", "Opération effectuée avec succès");
		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("message_error", ex.getMessage());
			return "user/edit_user_org";
		}

		return "user/edit_user_org";
	}

}
