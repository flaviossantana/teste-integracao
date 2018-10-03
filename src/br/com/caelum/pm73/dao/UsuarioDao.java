package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Leilao;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDao {

	private final Session session;

	public UsuarioDao(Session session) {
		this.session = session;
	}
	
	public Usuario porId(int id) {
		try {
			return (Usuario) session.get(Usuario.class, id);
		} catch (ObjectNotFoundException obj){
			return null;
		}
	}
	
	public Usuario porNomeEEmail(String nome, String email) {
		return (Usuario) session.createQuery("from Usuario u where u.nome = :nome and u.email = :email")
				.setParameter("nome", nome)
				.setParameter("email", email)
				.uniqueResult();
	}
	
	public void salvar(Usuario usuario) {
		session.save(usuario);
	}
	
	public void atualizar(Usuario usuario) {
		session.merge(usuario);
	}
	
	public void deletar(Usuario usuario) {
		session.delete(usuario);
	}
}
