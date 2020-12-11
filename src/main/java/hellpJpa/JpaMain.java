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
            detach();
            tx.commit();
        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close(); // data connection 물고 실행되기 때문에 꼭 닫아주는게 좋음
        }

        emf.close();
    }

    public static void detach(){
        // 영속상태
        Member member = em.find(Member.class, 12L);

        em.detach(member);

        member.setName("방탄소년단"); // 아무일도 일어나지 않는다.
    }

    public static void afterFlush(){
        Member member = new Member(12L, "방");
        em.persist(member);
        em.flush();
        System.out.println("=========");
    }

    //before flush
    public static void beforeFlush(){
        Member member = new Member(11L, "배고파");
        em.persist(member);

        System.out.println("=========");
    }

    // 엔티티 수정 변경 감지 - dirtychecking
    public static void update(String name){
        Member updateMember = em.find(Member.class, 10L);

        updateMember.setName(name);
    }

    //영속 비영속 확인 메소드
    public static void persistence(){
        // 비영속
        Member member = new Member();
        member.setId(10L);
        member.setName("박보검");

        //영속 -> 1차 캐시에 저장됨
        em.persist(member);

        // 조회용 sql문이 나갈까? -> db에서 가져오는 것이 아니라 1차캐시에서 가져오게 된
        Member findMember = em.find(Member.class, member.getId());

        System.out.println("findMember.id = " + findMember.getId());
        System.out.println("findMember.name = "+ findMember.getName());

    }

    // 두번 조회
    public static void selectTwice(){
        //처음 조회할때는 1차캐시에 없으므로 db에서 쿼리해서 가져온다
        Member findMember1 = em.find(Member.class, 10L);
        // 두번째 조회할때는 1차캐시 존재하므로 쿼리하지 않고 캐시에서 바로 가져온다(같은 식별자(primary key)
        Member findMember2 = em.find(Member.class, 10L);
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
