// Front-end interactions: add via AJAX, toggle complete and delete via fetch API
document.addEventListener('DOMContentLoaded', function() {
    const addAjaxBtn = document.getElementById('addAjaxBtn');
    const form = document.getElementById('taskForm');
    const tasksList = document.getElementById('tasksList');

    addAjaxBtn?.addEventListener('click', async () => {
        const title = document.getElementById('title').value.trim();
        const description = document.getElementById('description').value.trim();
        if (!title) {
            alert('Título é obrigatório (front-end)');
            return;
        }
        try {
            const resp = await fetch('/api/tasks', {
                method: 'POST',
                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                body: JSON.stringify({title, description})
            });
            if (!resp.ok) throw new Error('Erro ao adicionar');
            const data = await resp.json();
            prependTaskToList(data);
            form.reset();
        } catch (err) {
            console.error(err);
            alert('Erro ao adicionar tarefa via AJAX');
        }
    });

    // delegate toggle and delete
    tasksList?.addEventListener('click', async (e) => {
        if (e.target.matches('.toggle-complete')) {
            const id = e.target.getAttribute('data-id');
            try {
                const r = await fetch(`/tasks/${id}/toggle`, {method: 'POST'});
                const j = await r.json();
                if (j.ok) {
                    const span = e.target.closest('li').querySelector('span');
                    if (j.completed) span.classList.add('text-decoration-line-through');
                    else span.classList.remove('text-decoration-line-through');
                }
            } catch (err) { console.error(err); }
        } else if (e.target.matches('.btn-delete')) {
            const id = e.target.getAttribute('data-id');
            if (!confirm('Deseja apagar essa tarefa?')) return;
            try {
                const r = await fetch(`/tasks/${id}/delete`, {method: 'POST'});
                const j = await r.json();
                if (j.ok) {
                    const li = e.target.closest('li');
                    li.remove();
                } else {
                    alert('Não foi possível apagar');
                }
            } catch (err) { console.error(err); alert('Erro ao apagar'); }
        }
    });

    function prependTaskToList(task) {
        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-start';
        li.innerHTML = `
            <div>
                <div>
                    <input type="checkbox" data-id="${task.id}" class="toggle-complete me-2"/>
                    <span>${escapeHtml(task.title)}</span>
                </div>
                <div class="small text-muted">${escapeHtml(task.description || '')}</div>
            </div>
            <div>
                <a href="/tasks/${task.id}/edit" class="btn btn-sm btn-outline-secondary me-1">Editar</a>
                <button class="btn btn-sm btn-danger btn-delete" data-id="${task.id}">Apagar</button>
            </div>
        `;
        tasksList.insertBefore(li, tasksList.firstChild);
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.appendChild(document.createTextNode(text));
        return div.innerHTML;
    }
});
