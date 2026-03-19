package tools.vitruv.methodologisttemplate.consistency;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.Repository;

import c2.C2Repository;
import ticket.TicketRepository;
import uncertainty.UncertaintyAnnotationRepository;

public class RootObjectCorrespondenceHelper {

    public static TicketRepository getTicketRepositoryFromRessourceSet(EObject rootObject) {
        var resourceSet = rootObject.eResource().getResourceSet();
        var ticketRepositories = resourceSet.getResources().stream()
                .flatMap(resource -> resource.getContents().stream())
                .filter(content -> content instanceof TicketRepository)
                .map(content -> (TicketRepository) content)
                .toList();

        if (ticketRepositories.isEmpty()) {
            System.err.println("No TicketRepository found in the ResourceSet. - cannot establish correspondence.");
            return null;
        }

        // Assuming there's only one TicketRepository in the ResourceSet
        return ticketRepositories.get(0);
    }

    public static C2Repository getC2RepositoryFromRessourceSet(EObject rootObject) {
        var resourceSet = rootObject.eResource().getResourceSet();
        var c2Repositories = resourceSet.getResources().stream()
                .flatMap(resource -> resource.getContents().stream())
                .filter(content -> content instanceof C2Repository)
                .map(content -> (C2Repository) content)
                .toList();

        if (c2Repositories.isEmpty()) {
            System.err.println("No C2Repository found in the ResourceSet. - cannot establish correspondence.");
            return null;
        }

        // Assuming there's only one C2Repository in the ResourceSet
        return c2Repositories.get(0);
    }

    public static Repository getPCMRepositoryFromRessourceSet(EObject rootObject) {
        var resourceSet = rootObject.eResource().getResourceSet();
        var pcmRepositories = resourceSet.getResources().stream()
                .flatMap(resource -> resource.getContents().stream())
                .filter(content -> content instanceof Repository)
                .map(content -> (Repository) content)
                .toList();

        if (pcmRepositories.isEmpty()) {
            System.err.println("No PCM Repository found in the ResourceSet. - cannot establish correspondence.");
            return null;
        }

        // Assuming there's only one PCM Repository in the ResourceSet
        return pcmRepositories.get(0);
    }

    public static UncertaintyAnnotationRepository getUncertaintyAnnotationRepositoryFromRessourceSet(
            EObject rootObject) {
        var resourceSet = rootObject.eResource().getResourceSet();
        var uncertaintyAnnotationRepositories = resourceSet.getResources().stream()
                .flatMap(resource -> resource.getContents().stream())
                .filter(content -> content instanceof UncertaintyAnnotationRepository)
                .map(content -> (UncertaintyAnnotationRepository) content)
                .toList();

        if (uncertaintyAnnotationRepositories.isEmpty()) {
            System.err.println(
                    "No UncertaintyAnnotationRepository found in the ResourceSet. - cannot establish correspondence.");
            return null;
        }

        // Assuming there's only one UncertaintyAnnotationRepository in the ResourceSet
        return uncertaintyAnnotationRepositories.get(0);
    }

}
