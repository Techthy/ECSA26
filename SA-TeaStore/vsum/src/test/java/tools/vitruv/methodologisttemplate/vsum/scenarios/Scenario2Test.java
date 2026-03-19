package tools.vitruv.methodologisttemplate.vsum.scenarios;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.URI;
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
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import architectureDecisionRecord.ADIssue;
import architectureDecisionRecord.ADRRepository;
import architectureDecisionRecord.ArchitectureDecisionRecordPackage;
import c1.C1Package;
import c1.C1Repository;
import c2.C2Package;
import c2.C2Repository;
import c2.Container;
import ticket.Ticket;
import ticket.TicketPackage;
import ticket.TicketRepository;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.methodologisttemplate.vsum.TestUtil;
import uncertainty.Uncertainty;
import uncertainty.UncertaintyAnnotationRepository;
import uncertainty.UncertaintyPackage;

public class Scenario2Test {

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
	 * Scenario 2: Add Uncertainty to ADR Alternatives that affect C2 Containers
	 * GIVEN: a TeaStore system with ADRs, C2 model, PCM model, Ticket model and
	 * Uncertainty Annotations and their correspondences
	 * (the correspondences are established by the reactions defined in the
	 * consistency module when loading the models)
	 * The Architecture Decision Record has an Issue that that has three ADR
	 * Alternatives one of which is selected as an Outcome
	 * The Architecture Decision Record contains an ADR Alternative that is marked
	 * with an Uncertainty Annotation
	 * The expectations is that after loading the models there exists
	 * correspondences between:
	 * The ADR Alternative and ADIssue
	 * The ADIssue and the Ticket created for it
	 * The ADR Alternative and the C2 Containers affected by it
	 * The C2 Containers and the PCM Components
	 * WHEN: a ADR Alternative is annotated with an Uncertainty
	 * (already done in the uncertainty model that is loaded for this test)
	 * THEN: the Uncertainty is propagated to the affected C2 Containers, PCM
	 * Components, and Tickets
	 * 
	 * @param tempDir
	 */
	@Test
	void scenario2(@TempDir Path tempDir) {

		System.out.println("=================== Scenario 2 start ==================");
		VirtualModel vsum = TestUtil.createDefaultVirtualModel(tempDir);

		System.out.println("Load Models representing the scenario");
		registerRootObjects(vsum, tempDir);

		View uncertaintyView = TestUtil.getDefaultView(vsum,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class, ADRRepository.class,
						TicketRepository.class));

		System.out.println("Check Uncertainty Annotations were propagated correctly:");
		UncertaintyAnnotationRepository uncertaintyRepo = (UncertaintyAnnotationRepository) uncertaintyView
				.getRootObjects(UncertaintyAnnotationRepository.class).iterator().next();

		checkADRIssueUncertaintyPropagation(uncertaintyRepo);
		checkC2ContainerUncertaintyPropagation(uncertaintyRepo, "AuthService");
		checkC2ContainerUncertaintyPropagation(uncertaintyRepo, "BackendUI");
		checkTicketUncertaintyPropagation(uncertaintyRepo, "T-124");
		checkPCMComponentUncertaintyPropagation(uncertaintyRepo, "AuthService");
		checkPCMComponentUncertaintyPropagation(uncertaintyRepo, "BackendUI");

		// Check total number of uncertainties matches
		// There are initially 3 uncertainties defined in the uncertainty model
		// + 6 uncertainties propagated (1 ADIssue, 2 C2 Containers, 2 PCM Components, 1
		// Ticket)
		Assertions.assertEquals(9, uncertaintyRepo.getUncertainties().size());

		System.out.println("ALL TEST PASSED");
		System.out.println("=================== Scenario 2 end ==================");

	}

	private void registerRootObjects(VirtualModel virtualModel, Path filePath) {
		ResourceSet resourceSet = new ResourceSetImpl();

		URIConverter.URI_MAP.put(
				URI.createURI("teastore_scenario2.c2"),
				URI.createFileURI("./src/test/resources/teastore_scenario2.c2"));
		URIConverter.URI_MAP.put(
				URI.createURI("teastore_scenario2.architecturedecisionrecord"),
				URI.createFileURI("./src/test/resources/teastore_scenario2.architecturedecisionrecord"));
		URL restype = Thread.currentThread().getContextClassLoader()
				.getResource("defaultModels/Palladio.resourcetype");
		URL primtype = Thread.currentThread().getContextClassLoader()
				.getResource("defaultModels/PrimitiveTypes.repository");
		URIConverter.URI_MAP.put(
				URI.createURI("pathmap://PCM_MODELS/Palladio.resourcetype"), URI.createURI(restype.toExternalForm()));
		URIConverter.URI_MAP.put(
				URI.createURI("pathmap://PCM_MODELS/PrimitiveTypes.repository"),
				URI.createURI(primtype.toExternalForm()));

		Resource c1Resource = TestUtil.loadResource(resourceSet, "./src/test/resources/teastore_scenario2.c1");
		Resource c2Resource = TestUtil.loadResource(resourceSet, "./src/test/resources/teastore_scenario2.c2");
		Resource adrResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario2.architecturedecisionrecord");
		Resource ticketResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario2.ticket");
		Resource uncertaintyResource = TestUtil.loadResource(resourceSet,
				"./src/test/resources/teastore_scenario2.uncertainty");
		Resource pcmResource = TestUtil.loadResource(resourceSet, "./src/test/resources/teastore_scenario2.repository");

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
		TestUtil.registerRootObject(virtualModel, filePath, adrRepo, List.of(ADRRepository.class, C2Repository.class));
		TestUtil.registerRootObject(virtualModel, filePath, uncertaintyRepo,
				List.of(UncertaintyAnnotationRepository.class, C2Repository.class, ADRRepository.class));

	}

	private void checkADRIssueUncertaintyPropagation(UncertaintyAnnotationRepository uncertaintyRepo) {
		List<Uncertainty> adIssueUncertainties = getUncertaintyWithReferenceTo(uncertaintyRepo, ADIssue.class);
		adIssueUncertainties.stream().filter(
				u -> ((ADIssue) u.getUncertaintyLocation().getReferencedComponents().get(0)).getId().equals("ADR-283"))
				.findAny()
				.orElseThrow(() -> new AssertionError("Expected Uncertainty referencing ADIssue ADR-283 not found."));
		System.out.println("	• ADIssue PASSED: Uncertainty referencing ADIssue ADR-283 found.");
	}

	private void checkC2ContainerUncertaintyPropagation(UncertaintyAnnotationRepository uncertaintyRepo,
			String containerName) {
		List<Uncertainty> c2ContainerUncertainties = getUncertaintyWithReferenceTo(uncertaintyRepo,
				Container.class);
		c2ContainerUncertainties.stream().filter(
				u -> ((Container) u.getUncertaintyLocation().getReferencedComponents().get(0)).getName()
						.equals(containerName))
				.findAny()
				.orElseThrow(() -> new AssertionError(
						"Expected Uncertainty referencing C2 Container '" + containerName + "' not found."));
		System.out.println(
				"	• C2 Container PASSED: Uncertainty referencing C2 Container '" + containerName + "' found.");
	}

	private void checkTicketUncertaintyPropagation(UncertaintyAnnotationRepository uncertaintyRepo, String ticketId) {
		List<Uncertainty> ticketUncertainties = getUncertaintyWithReferenceTo(uncertaintyRepo,
				Ticket.class);
		ticketUncertainties.stream().filter(
				u -> ((Ticket) u.getUncertaintyLocation().getReferencedComponents().get(0)).getId()
						.equals(ticketId))
				.findAny()
				.orElseThrow(() -> new AssertionError(
						"Expected Uncertainty referencing Ticket '" + ticketId + "' not found."));
		System.out.println("	• Ticket PASSED: Uncertainty referencing Ticket '" + ticketId + "' found.");
	}

	private void checkPCMComponentUncertaintyPropagation(UncertaintyAnnotationRepository uncertaintyRepo,
			String componentName) {
		List<Uncertainty> pcmComponentUncertainties = getUncertaintyWithReferenceTo(uncertaintyRepo,
				BasicComponent.class);
		pcmComponentUncertainties.stream().filter(
				u -> ((BasicComponent) u.getUncertaintyLocation().getReferencedComponents().get(0)).getEntityName()
						.equals(componentName))
				.findAny()
				.orElseThrow(() -> new AssertionError(
						"Expected Uncertainty referencing PCM BasicComponent '" + componentName + "' not found."));
		System.out.println(
				"	• PCM Component PASSED: Uncertainty referencing PCM BasicComponent '" + componentName + "' found.");
	}

	private List<Uncertainty> getUncertaintyWithReferenceTo(UncertaintyAnnotationRepository uncertaintyRepo,
			Class<?> referencedType) {
		return uncertaintyRepo.getUncertainties().stream()
				.filter(uncertainty -> uncertainty.getUncertaintyLocation().getReferencedComponents().stream()
						.anyMatch(referencedType::isInstance))
				.toList();
	}

}
