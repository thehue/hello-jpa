package hellpJpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    static EntityManager em = emf.createEntityManager();
    static EntityTransaction tx = em.getTransaction();

    public static void main(String[] args) {
        try{
            tx.begin();
            List<Member> pageList = findByPagination(3, 0);
            System.out.println(pageList);
            tx.commit();
        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close(); // data connection 물고 실행되기 때문에 꼭 닫아주는게 좋음
        }

        emf.close();
    }

    public static void createMember(int start, int end){
        for(int i=start; i<= end; i++){
            Member member = new Member();

            member.setId((long) i);
            member.setName("이름"+i);

            em.persist(member);
        }
    }

    public static Member findMemberById(Long id){

        return em.find(Member.class, id);
    }

    public static Member updateMemberNameById(Long id, String name){
        Member findMember = findMemberById(id);
        findMember.setName(name);

        return findMember;
    }

    public static void removeMemberById(Long id){
        Member findMember = findMemberById(id);

        em.remove(findMember);
    }

    public static List<Member> findAll(){
        String ql = "select m from Member as m";
        return em.createQuery(ql, Member.class).getResultList();
    }

    public static void printAllMembers(List<Member> resultList){
        for(Member result: resultList){
            System.out.println("member.name = "+result.getName());
        }
    }

    public static List<Member> findByPagination(int limit, int offset){
        return em.createQuery("select m from Member as m", Member.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
