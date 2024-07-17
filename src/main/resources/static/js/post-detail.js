document.addEventListener('DOMContentLoaded', function () {
    const postHeader = document.querySelector('.post_header');
    const deleteButton = document.querySelector('.deleteButton');
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = `
        <div class="modal_block">
          <div class="modal_wrapper">
            <div class="deletePost_modal">
              <h3>포스트 삭제</h3>
              <div class="delete_message">정말로 삭제하시겠습니까?</div>
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

    modalContainer.querySelector('.cancel_button').addEventListener('click', function() {
        document.body.removeChild(modalContainer);
    });

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