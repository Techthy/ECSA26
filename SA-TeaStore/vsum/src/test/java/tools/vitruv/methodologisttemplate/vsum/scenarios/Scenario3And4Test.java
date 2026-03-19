package tools.vitruv.methodologisttemplate.vsum.scenarios;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import architectureDecisionRecord.ADAlternative;
import architectureDecisionRecord.ADRRepository;
import architectureDecisionRecord.ArchitectureDecisionRecordPackage;
import c1.C1Package;
import c1.C1Repository;
import c2.C2Package;
import c2.C2Repository;
import ticket.TicketPackage;
import ticket.TicketRepository;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.methodologisttemplate.consistency.UncertaintyReactionsHelper;
import tools.vitruv.methodologisttemplate.vsum.TestUtil;
import uncertainty.Uncertainty;
import uncertainty.UncertaintyAnnotationRepository;
import uncertainty.UncertaintyPackage;

public class Scenario3And4Test {

	@BeforeAll
	static void setup() {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*",
				new XMIResourceFactoryImpl());

		var pkgReg = EPackage.Registry.INSTANCE;
		pkgReg.put(RepositoryPackage.eNS_URI, RepositoryPackage.eINSTANCE);
		pkgReg.put(C1Package.eNS_URI, C1Package.eINSTANCE);
		pkgReg.put(C2Package.eNS_URI, C2Package.eINSTANCE);
		pkgReg.put(TicketPackage.eNS_URI, TicketPackage.eINSTANCE);
		pkgReg.put(UncertaintyPackage.eNS_URI, UncertaintyPackage.eINSTANCE);
		pkgReg.put(ArchitectureDecisionRecordPackage.eNS_URI, ArchitectureDecisionRecordPackage.eINSTANCE);
	}

	/**
	 * Removes and re-adds an Uncertainty referencing an ADAlternative, expecting it
	 * to be
	 * re-created like normal.
	 * 
	 * @param tempDir
	 */
	@Test
	void scenario3(@TempDir Path tempDir) {

		System.out.println("=================== Scenario 3 start ==================");
		VirtualModel vsum = TestUtil.createDefaultVirtualModel(tempDir);

		System.out.println("Load Models representing the scenario");
		registerRootObjects(vsum, tempDir);

		Uncertainty adAlternativeUncertainty = getADAlternativeUncertainty(vsum);
		Uncertainty tobeReAdded = UncertaintyReactionsHelper.deepCopyUncertainty(adAlternativeUncertainty);
		EObject referencedComponent = adAlternativeUncertainty.getUncertaintyLocation()
				.getReferencedComponents().get(0);
		tobeReAdded.getUncertaintyLocation().getReferencedComponents().clear();

		expectADAlternativeUncertaintyToExist(vsum);
		removeUncertainty(vsum, adAlternativeUncertainty);
		expectADAlternativeUncertaintyToNotExist(vsum);
		addUncertainty(vsum, tobeReAdded, referencedComponent, false);
		expectADAlternativeUncertaintyToExist(vsum);

		expectNumberOfUncertaintiesToBe(vsum, 8);
		System.out.println("=================== Scenario 3 end ==================");

	}

	/**
	 * Removes and re-adds an Uncertainty referencing an ADAlternative, expecting it
	 * to not be re-created due to automated resolution.
	 * 
	 * @param tempDir
	 */
	@Test
	void scenario4(@TempDir Path tempDir) {

		System.out.println("=================== Scenario 4 start ==================");
		VirtualModel vsum = TestUtil.createDefaultVirtualModel(tempDir);

		System.out.println("Load Models representing the scenario");
		registerRootObjects(vsum, tempDir);

		Uncertainty adAlternativeUncertainty = getADAlternativeUncertainty(vsum);
		Uncertainty tobeReAdded = UncertaintyReactionsHelper.deepCopyUncertainty(adAlternativeUncertainty);
		EObject referencedComponent = adAlternativeUncertainty.getUncertaintyLocation()
				.getReferencedComponents().get(0);
		tobeReAdded.getUncertaintyLocation().getReferencedComponents().clear();

		expectADAlternativeUncertaintyToExist(vsum);
		removeUncertainty(vsum, adAlternativeUncertainty);
		expectADAlternativeUncertaintyToNotExist(vsum);
		addUncertainty(vsum, tobeReAdded, referencedComponent, true);
		expectADAlternativeUncertaintyToNotExist(vsum);

		expectNumberOfUncertaintiesToBe(vsum, 0);
		System.out.println("=================== Scenario 4 end ==================");

	}

	private void registerRootObjects(VirtualModel virtualModel, Path filePath) {
		ResourceSet resourceSet = new ResourceSetImpl();

		URIConverter.URI_MAP.put(
				URI.createURI("teastore_scenario1.c2"),
				URI.createFileURI("./src/test/resources/teastore_scenario1.c2"));
		URIConverter.URI_MAP.put(
				URI.createURI("teastore_scenario1.architecturedecisionrecord"),
				URI.createFileURI(
						"./src/test/resources/teastore_scenario1.architecturedecisionrecord"));
		URL restype = Thread.currentThread().getContextClassLoader()
				.getResource("defaultModels/Palladio.resourcetype");
		URL primtype = Thread.currentThread().getContextClassLoader()
				.getResource("defaultModels/PrimitiveTypes.repository");
		URIConverter.URI_MAP.put(
				URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype"),
				URI.createURI(restype.toExternalForm()));
		URIConverter.URI_MAP.put(
				URI.createURI("pathmap://PCM_MODELS/PrimitiveTypes.repository"),
				URI.createURI(primtype.toExternalForm()));

		Resource c1Resource = TestUtil.loadResource(resourceSet, "./src/test/resources/teastore_scenario1.c1");
		Resource c2Resource = TestUtil.loadResource(resourceSet, "./src/test/resources/teastore_scenario1.c2");
		Resource adrResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario1.architecturedecisionrecord");
		Resource ticketResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario1.ticket");
		Resource uncertaintyResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario1.uncertainty");
		Resource pcmResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario1.repository");

		// Get the root object of the loaded resource
		ADRRepository adrRepo = (ADRRepository) adrResource.getContents().get(0);
		C1Repository c1Repo = (C1Repository) c1Resource.getContents().get(0);
		C2Repository c2Repo = (C2Repository) c2Resource.getContents().get(0);
		TicketRepository ticketRepo = (TicketRepository) ticketResource.getContents().get(0);
		UncertaintyAnnotationRepository uncertaintyRepo = (UncertaintyAnnotationRepository) uncertaintyResource
				.getContents().get(0);
		Repository pcmRepo = (Repository) pcmResource.getContents().get(0);

		TestUtil.registerRootObject(virtualModel, filePath, c1Repo, List.of(C1Repository.class));
		TestUtil.registerRootObject(virtualModel, filePath, pcmRepo, List.of(Repository.class));
		TestUtil.registerRootObject(virtualModel, filePath, c2Repo, List.of(C2Repository.class));
		TestUtil.registerRootObject(virtualModel, filePath, ticketRepo,
				List.of(TicketRepository.class));
		TestUtil.registerRootObject(virtualModel, filePath, adrRepo,
				List.of(ADRRepository.class, C2Repository.class));
		TestUtil.registerRootObject(virtualModel, filePath, uncertaintyRepo,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class,
						ADRRepository.class));

	}

	private void expectNumberOfUncertaintiesToBe(
			VirtualModel vsum, int expectedNumber) {
		View uncertaintyView = TestUtil.getDefaultView(vsum,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class, ADRRepository.class,
						TicketRepository.class));
		UncertaintyAnnotationRepository uncertaintyRepo = (UncertaintyAnnotationRepository) uncertaintyView
				.getRootObjects(UncertaintyAnnotationRepository.class).iterator().next();
		Assertions.assertEquals(expectedNumber, uncertaintyRepo.getUncertainties().size(),
				"Expected number of Uncertainties not found in UncertaintyAnnotationRepository.");
	}

	private void removeUncertainty(VirtualModel vsum, Uncertainty uncertaintyToBeRemoved) {
		CommittableView view = TestUtil.getDefaultView(vsum,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class, ADRRepository.class,
						TicketRepository.class))
				.withChangeRecordingTrait();
		modifyView(view, (CommittableView v) -> {
			UncertaintyAnnotationRepository uncertaintyRepo2 = (UncertaintyAnnotationRepository) v
					.getRootObjects(UncertaintyAnnotationRepository.class).iterator().next();
			uncertaintyRepo2.getUncertainties().removeIf(u -> u.getId().equals(uncertaintyToBeRemoved.getId()));
			// uncertaintyRepo2.getUncertainties().get(0).setSetManually(false);
		});
	}

	private void addUncertainty(VirtualModel vsum, Uncertainty tobeReAdded, EObject referencedComponent,
			Boolean automatedResolution) {
		CommittableView view = TestUtil.getDefaultView(vsum,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class, ADRRepository.class,
						TicketRepository.class))
				.withChangeRecordingTrait();
		modifyView(view, (CommittableView v) -> {
			UncertaintyAnnotationRepository uncertaintyRepo = (UncertaintyAnnotationRepository) v
					.getRootObjects(UncertaintyAnnotationRepository.class).iterator().next();
			tobeReAdded.setSetManually(true);
			uncertaintyRepo.getUncertainties().add(tobeReAdded);
			uncertaintyRepo.getHistory().setAutomatedResolution(automatedResolution);
			tobeReAdded.getUncertaintyLocation().getReferencedComponents().add(referencedComponent);
		});
	}

	private void expectADAlternativeUncertaintyToExist(
			VirtualModel vsum) {
		Uncertainty adAlternativeUncertainty = getADAlternativeUncertainty(vsum);
		Assertions.assertNotNull(adAlternativeUncertainty,
				"Expected Uncertainty referencing ADAlternative 'Reuse Existing Redis Cache for Product Data' not found.");
	}

	private void expectADAlternativeUncertaintyToNotExist(
			VirtualModel vsum) {
		Uncertainty adAlternativeUncertainty = getADAlternativeUncertainty(vsum);
		Assertions.assertNull(adAlternativeUncertainty,
				"Uncertainty referencing ADAlternative 'Reuse Existing Redis Cache for Product Data' was not expected to be found.");
	}

	private Uncertainty getADAlternativeUncertainty(VirtualModel vsum) {
		View uncertaintyView = TestUtil.getDefaultView(vsum,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class, ADRRepository.class,
						TicketRepository.class));
		UncertaintyAnnotationRepository uncertaintyRepo = (UncertaintyAnnotationRepository) uncertaintyView
				.getRootObjects(UncertaintyAnnotationRepository.class).iterator().next();
		List<Uncertainty> adrUncertainties = getUncertaintyWithReferenceTo(uncertaintyRepo, ADAlternative.class);
		return adrUncertainties.stream().filter(
				u -> ((ADAlternative) u.getUncertaintyLocation().getReferencedComponents().get(0)).getName()
						.equals("Reuse Existing Redis Cache for Product Data"))
				.findAny().orElse(null);
	}

	private static void modifyView(CommittableView view, Consumer<CommittableView> modificationFunction) {
		modificationFunction.accept(view);
		view.commitChanges();
	}

	private List<Uncertainty> getUncertaintyWithReferenceTo(UncertaintyAnnotationRepository uncertaintyRepo,
			Class<?> referencedType) {
		return uncertaintyRepo.getUncertainties().stream()
				.filter(uncertainty -> uncertainty.getUncertaintyLocation().getReferencedComponents()
						.stream()
						.anyMatch(referencedType::isInstance))
				.toList();
	}

}
