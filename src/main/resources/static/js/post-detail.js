document.addEventListener('DOMContentLoaded', function () {
    const deleteButton = document.querySelector('.deleteButton');
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = `
        <div class="modal_overlay"></div>
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

    if (deleteButton) {
        deleteButton.addEventListener('click', function () {
            document.body.appendChild(modalContainer);
        });
    }

    const closeModal = function () {
        document.body.removeChild(modalContainer);
    };

    modalContainer.querySelector('.modal_overlay').addEventListener('click', closeModal);
    modalContainer.querySelector('.cancel_button').addEventListener('click', closeModal);

    modalContainer.querySelector('.confirm_button').addEventListener('click', function () {
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
    });

    // 태그 부분
    const initialTags = document.querySelector('input[name="tags"]').value;
    const tagConainer = document.querySelector('.tag_wrapper');
    let tags = initialTags ? initialTags.split(',') : [];

    function renderTags() {
        tagConainer.innerHTML = tags.map(tag => `
            <a href="/#" class="tag" data-tag="${tag}">${tag}</a>
        `).join('');
    }

    renderTags();

    // 댓글 부분
    document.querySelector('.comment_submit').addEventListener('click', function () {
        const commentText = document.querySelector('.comment_input').value;
        const postId = document.querySelector('input[name="postId"]').value;
        const userId = document.getElementById('signedInUserId').value;
        const username = document.getElementById('signedInUsername').value;
        const domain = document.getElementById('signedInDomain').value;
        const profileImage = document.getElementById('signedInProfileImage').value;
        console.log('userId: ' + userId);
        if (!userId) {
            window.location.href = '/login';
        }

        fetch(`/api/posts/${postId}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                comment: commentText,
                user: {
                    id: userId,
                    username: username,
                    domain: domain,
                    profileImage: profileImage
                }
            }),
        })
            .then(response => response.json())
            .then(data => {
                console.log('Success:', data);
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    console.error('No redirect URL provided');
                    alert('댓글 작성은 완료되었지만 리다이렉트 URL이 없습니다.');
                }
            })
            .catch(error => console.error('Error: ', error));
    });
});

// 댓글 삭제 함수
function deleteComment(commentId) {
    if (confirm('댓글을 정말로 삭제하시겠습니까?')) {
        fetch(`/api/comments/${commentId}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    console.error('No redirect URL provided');
                    alert('댓글이 삭제되었지만 리다이렉트 URL이 없습니다.');
                }
            })
            .catch(error => {
                console.error('Error: ', error);
                alert('댓글 삭제 중 오류가 발생했습니다.');
            })
    }
}

function editComment(commentId) {
    const contentBlock = document.getElementById('comment-content-' + commentId);
    const originalContent = contentBlock.querySelector('.real_comment_content p').innerText;

    contentBlock.innerHTML = `
        <div class="comment_update_wrapper">
          <textarea class="comment_update_input" placeholder="댓글을 작성하세요.">${originalContent}</textarea>
            <div class="cancel_update_wrapper">
                <button class="update_cancel" onclick="cancelEdit(${commentId}, '${originalContent}')">취소</button>
                <button class="do_update" onclick="updateComment(${commentId})">댓글 수정</button>
            </div>
        </div>
    `
}

function cancelEdit(commentId, originalContent) {
    const contentBlock = document.getElementById('comment-content-' + commentId);
    contentBlock.innerHTML = `
        <div class="comment_content_wrapper">
            <div class="comment_content">
                <div class="real_comment_content">
                    <p>${originalContent}</p>
                </div>
            </div>
        </div>
    `
}

function updateComment(commentId) {
    const updatedContent = document.querySelector(
        `#comment-content-${commentId} .comment_update_input`
    ).value;

    fetch(`/api/comments/${commentId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({comment: updatedContent})
    })
        .then(response => response.json())
        .then(data => {
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                console.error('No redirect URL provided');
                alert('댓글이 수정되었지만 리다이렉트 URL이 없습니다.');
            }
        })
        .catch(error => {
            console.error('Error: ', error);
            alert('댓글 수정 중 오류가 발생했습니다.');
        })
}