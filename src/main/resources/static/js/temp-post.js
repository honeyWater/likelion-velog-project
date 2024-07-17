document.addEventListener('DOMContentLoaded', function () {
    const deleteButton = document.querySelector('.remove');
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = `
        <div class="modal_overlay"></div>
        <div class="modal_block">
          <div class="modal_wrapper">
            <div class="deletePost_modal">
              <h3>임시 글 삭제</h3>
              <div class="delete_message">임시 저장한 글을 삭제하시겠습니까?<br>삭제한 글은 복구할 수 없습니다.</div>
              <div class="delete_buttonArea">
                <button class="cancel_button">취소</button>
                <button class="confirm_button">확인</button>
              </div>
            </div>
          </div>
        </div>
    `;

    deleteButton.addEventListener('click', function() {
        document.body.appendChild(modalContainer);
    });

    const closeModal = function() {
        document.body.removeChild(modalContainer);
    };

    modalContainer.querySelector('.modal_overlay').addEventListener('click', closeModal);
    modalContainer.querySelector('.cancel_button').addEventListener('click', closeModal);

    modalContainer.querySelector('.confirm_button').addEventListener('click', function() {
        const postId = document.querySelector('input[name="postId"]').value;
        fetch(`/api/posts/delete/${postId}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    window.location.href = '/';
                } else {
                    alert('삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error: ', error);
                alert('오류가 발생했습니다.');
            })
    })
})