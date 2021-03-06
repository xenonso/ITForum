package itforum.repositories_implementation;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import itforum.entities.ForumPost;
import itforum.repositories.ForumPostRepository;

@Repository
@Transactional
public class ForumPostRepositoryImpl implements ForumPostRepository{

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void savePost(ForumPost forumPost) {
		entityManager.merge(forumPost);
	}
	
	@Override
	public Long countPosts() {
		Long numberOfPosts = (Long) entityManager.createQuery("SELECT COUNT(*) FROM ForumPost p").getSingleResult();
		return numberOfPosts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<ForumPost> findAllPostsByCategorySortByNewest(String category) {
		Query query = entityManager.createNativeQuery(
				  "SELECT fp.id, fp.title, fp.content, fp.date FROM forumpost AS fp "
				+ "INNER JOIN forumcategory AS fc "
				+ "ON fc.id = fp.idCategory "
				+ "WHERE fc.title=:title "
				+ "ORDER BY fp.date DESC");
		query.setParameter("title", category);
		
		List<Object[]> postsInCategory = query.getResultList();
		LinkedList<ForumPost> postsInCategoryMapped = mapToPostList(postsInCategory);
		
		return postsInCategoryMapped;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<ForumPost> findAllPostsById(Long id) {
		Query query = entityManager.createNativeQuery(
				  "SELECT fp.id, fp.title, fp.content, fp.date FROM forumpost AS fp "
				+ "INNER JOIN forumcategory AS fc "
				+ "ON fc.id = fp.idCategory "
				+ "WHERE fc.id=?1");
		query.setParameter(1, id);
		
		List<Object[]> postsInCategory = query.getResultList();
		LinkedList<ForumPost> postsInCategoryMapped = mapToPostList(postsInCategory);
		
		return postsInCategoryMapped;
	}
	
	private LinkedList<ForumPost> mapToPostList(List<Object[]> posts){
		LinkedList<ForumPost> mappedPosts = new LinkedList<ForumPost>();	
		
		for(Object[] post : posts){	
			mappedPosts.add(
					new ForumPost(
							((BigInteger)post[0]).longValue(),
							(String) post[1],
							(String) post[2],
							(Timestamp) post[3]
					));
		}
		
		return mappedPosts;
	}

	@Override
	public ForumPost findPostById(Long id) {
		try{
			ForumPost post = entityManager.createQuery("SELECT p FROM ForumPost p WHERE id=:postId", ForumPost.class)
					.setParameter("postId", id)
					.getSingleResult();
			return post;
		}catch(NoResultException nre){
			return null;
		}
		
	}
}
