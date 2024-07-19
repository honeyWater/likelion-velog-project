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
            <a href="/static#" class="tag" data-tag="${tag}">${tag}</a>
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

        if (!userId) {
            alert('로그인 후 이용해 주시길 바랍니다.');
            window.location.href = '/loginform';
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

    // 페이지 로드 시 'n개의 답글', '답글 달기' 같은 원래 텍스트 저장
    const replyTexts = document.querySelectorAll('.reply_text');
    replyTexts.forEach(text => {
        text.setAttribute('data-original-text', text.textContent);
    });

    // 답글 작성하기 버튼에 이벤트 리스너 추가
    const newReplyButtons = document.querySelectorAll('.new_reply_button');
    newReplyButtons.forEach(button => {
        button.addEventListener('click', function() {
            const commentId = this.closest('.comment_wrapper').id.split('-')[1];
            if (commentId) {
                showNewReplyForm(commentId);
            } else {
                console.error('Could not find comment ID');
            }
        });
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

function toggleReplyForm(commentId) {
    const commentBlock = document.querySelector(`#comment-${commentId}`);
    const replyButton = commentBlock.querySelector('.reply_button');
    const replyText = replyButton.querySelector('.reply_text');
    const plusPath = replyButton.querySelector('.plus_path');
    const minusPath = replyButton.querySelector('.minus_path');
    const allReplyBlock = commentBlock.querySelector('.all_reply_block');
    const replyWriteWrapper = commentBlock.querySelector('.reply_write_wrapper');
    const newReplyButton = commentBlock.querySelector('.new_reply_button');

    const hasReplies = allReplyBlock && allReplyBlock.querySelector('.each_reply_wrapper');

    if (hasReplies) {
        // 대댓글이 있는 경우
        if (allReplyBlock.style.display === 'none' || allReplyBlock.style.display === '') {
            allReplyBlock.style.display = 'block';
            newReplyButton.style.display = 'block';  // 답글 작성하기 버튼 표시
            replyText.textContent = '숨기기';
            plusPath.style.display = 'none';
            minusPath.style.display = 'block';
        } else {
            allReplyBlock.style.display = 'none';
            newReplyButton.style.display = 'none';  // 답글 작성하기 버튼 숨김
            replyText.textContent = replyText.getAttribute('data-original-text');
            plusPath.style.display = 'block';
            minusPath.style.display = 'none';
        }
    } else {
        // 대댓글이 없는 경우
        if (replyWriteWrapper.style.display === 'none' || replyWriteWrapper.style.display === '') {
            replyWriteWrapper.style.display = 'block';
            replyText.textContent = '숨기기';
            plusPath.style.display = 'none';
            minusPath.style.display = 'block';
        } else {
            replyWriteWrapper.style.display = 'none';
            replyText.textContent = '답글 달기';
            plusPath.style.display = 'block';
            minusPath.style.display = 'none';
        }
    }
}

function cancelReply(commentId) {
    const commentBlock = document.querySelector(`#comment-${commentId}`);
    const replyWriteWrapper = commentBlock.querySelector('.reply_write_wrapper');
    const newReplyButton = commentBlock.querySelector('.new_reply_button');
    const replyButton = commentBlock.querySelector('.reply_button');
    const replyText = replyButton.querySelector('.reply_text');
    const plusPath = replyButton.querySelector('.plus_path');
    const minusPath = replyButton.querySelector('.minus_path');

    replyWriteWrapper.style.display = 'none';
    replyWriteWrapper.querySelector('.reply_textarea').value = '';

    const hasReplies = commentBlock.querySelector('.all_reply_block .each_reply_wrapper');
    if (hasReplies) {
        newReplyButton.style.display = 'block';
    }

    replyText.textContent = replyText.getAttribute('data-original-text') || '답글 달기';
    plusPath.style.display = 'block';
    minusPath.style.display = 'none';
}

function submitReply(commentId) {
    const replyForm = document.getElementById('reply-form-' + commentId);
    const replyContent = replyForm.querySelector('.reply_textarea').value;
    const userId = document.getElementById('signedInUserId').value;
    const username = document.getElementById('signedInUsername').value;
    const domain = document.getElementById('signedInDomain').value;
    const profileImage = document.getElementById('signedInProfileImage').value;

    fetch('/api/comments/' + commentId + '/replies', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            comment: replyContent,
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
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                console.error('No redirect URL provided');
                alert('대댓글이 등록되었지만 리다이렉트 URL이 없습니다.');
            }
        })
        .catch(error => {
            console.error('Error: ', error);
            alert('로그인 후 이용해주시기 바랍니다.');
        });

    // 폼 숨기기 및 초기화
    cancelReply(commentId);
}

function editReply(replyId) {
    const contentBlock = document.querySelector(`#reply-content-${replyId}`);
    const originalContent = contentBlock.querySelector('p').innerText;

    contentBlock.innerHTML = `
        <div class="reply_update_wrapper">
          <textarea class="reply_update_input" placeholder="답글을 작성하세요.">${originalContent}</textarea>
          <div class="cancel_update_wrapper">
            <button class="update_cancel" onclick="cancelReplyEdit(${replyId}, '${originalContent.replace(/'/g, "\\'")}')">취소</button>
            <button class="do_update" onclick="updateReply(${replyId})">답글 수정</button>
          </div>
        </div>
    `;
}

function cancelReplyEdit(replyId, originalContent) {
    const contentBlock = document.querySelector(`#reply-content-${replyId}`);
    contentBlock.innerHTML = `<p>${originalContent}</p>`;
}

function updateReply(replyId) {
    const updatedContent = document.querySelector(
        `#reply-content-${replyId} .reply_update_input`
    ).value;

    fetch(`/api/replies/${replyId}`, {
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
                alert('답글이 수정되었지만 리다이렉트 URL이 없습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('답글 수정 중 오류가 발생했습니다.');
        });
}

function deleteReply(replyId) {
    if (confirm('답글을 정말로 삭제하시겠습니까?')) {
        fetch(`/api/replies/${replyId}`, {
            method: 'DELETE',
        })
            .then(response => response.json())
            .then(data => {
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    console.error('No redirect URL provided');
                    alert('답글이 삭제되었지만 리다이렉트 URL이 없습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('답글 삭제 중 오류가 발생했습니다.');
            });
    }
}

function showNewReplyForm(commentId) {
    const commentBlock = document.querySelector(`#comment-${commentId}`);
    if (!commentBlock) {
        console.error(`Comment block with id 'comment-${commentId}' not found`);
        return;
    }
    const replyWriteWrapper = commentBlock.querySelector('.reply_write_wrapper');
    const newReplyButton = commentBlock.querySelector('.new_reply_button');

    if (replyWriteWrapper) {
        replyWriteWrapper.style.display = 'block';
    }
    if (newReplyButton) {
        newReplyButton.style.display = 'none';
    }
}

