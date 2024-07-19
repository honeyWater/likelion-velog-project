let inPrivate = false; // 전역 변수로 선언

function initializeEventListeners() {
    const reuploadButton = document.querySelector('.reuploadButton');
    const removeButton = document.querySelector('.removeButton');
    const reuploadInput = document.getElementById('reuploadInput');

    const thumbnailUpload = document.getElementById('thumbnailUpload');
    const noPrivateButton = document.querySelector('.no_private');
    const yesPrivateButton = document.querySelector('.yes_private');
    const publishButton = document.querySelector('.real_publish');

    // description 글자 수 표시
    const description = document.getElementById('description');
    const countText = document.querySelector('.count_text');
    function updateCharCount() {
        const count = description.value.length;
        countText.textContent = `${count} / 150`;
    }
    description.addEventListener('input', updateCharCount);
    // 초기 로드 시 글자 수 표시
    updateCharCount();

    if (thumbnailUpload) {
        thumbnailUpload.addEventListener('change', handleThumbnailUpload);
    }

    if (reuploadButton){
        reuploadButton.addEventListener('click', function() {
            reuploadInput.click();
        });
    }

    if (reuploadInput) {
        reuploadInput.addEventListener('change', handleThumbnailReupload);
    }

    if (removeButton) {
        removeButton.addEventListener('click', handleThumbnailRemove);
    }

    if (noPrivateButton && yesPrivateButton) {
        noPrivateButton.addEventListener('click', function () {
            inPrivate = false;
            this.classList.add('active');
            yesPrivateButton.classList.remove('active');
        });

        yesPrivateButton.addEventListener('click', function () {
            inPrivate = true;
            this.classList.add('active');
            noPrivateButton.classList.remove('active');
        });
    }

    if (publishButton) {
        publishButton.addEventListener('click', handlePublish);
    }
}

function handleThumbnailUpload(event) {
    event.preventDefault();

    const file = event.target.files[0];
    const postId = document.querySelector('input[name="postId"]').value;

    if (file) {
        const formData = new FormData();
        formData.append('thumbnail', file);
        formData.append('postId', postId);

        fetch('/api/posts/upload-thumbnail', {
            method: 'POST',
            body:formData
        })
            .then(response => response.json())
            .then(data => {
                if(data.redirectUrl){
                    window.location.href = data.redirectUrl;
                } else {
                    console.error('No redirect URL provided');
                    alert('썸네일 업로드는 완료되었지만 리다이렉트 URL이 없습니다.');
                }
            })
            .catch(error => console.error('Error: ', error));
    }
}

function handleThumbnailReupload(event) {
    const file = event.target.files[0];
    const postId = document.querySelector('input[name="postId"]').value;

    if (file) {
        const formData = new FormData();
        formData.append('thumbnail', file);
        formData.append('postId', postId);

        fetch('/api/posts/reupload-thumbnail', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if(data.redirectUrl){
                    window.location.href = data.redirectUrl;
                } else {
                    console.error('No redirect URL provided');
                    alert('썸네일 재업로드는 완료되었지만 리다이렉트 URL이 없습니다.');
                }
            })
            .catch(error => {
                console.error('Error: ', error);
                alert('썸네일 업데이트 중 오류가 발생했습니다.');
            });
    }
}

function handleThumbnailRemove() {
    const postId = document.querySelector('input[name="postId"]').value;

    fetch('/api/posts/remove-thumbnail', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ postId: postId })
    })
        .then(response => response.json())
        .then(data => {
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
                console.log('썸네일이 성공적으로 제거되었습니다.');
            } else {
                console.log('썸네일 제거에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error: ', error);
            alert('썸네일 제거 중 오류가 발생했습니다.');
        });
}

function handlePublish(e) {
    const postId = document.querySelector('input[name="postId"]').value;
    const thumbnailImage = document.getElementById('thumbnailImage').value;
    const description = document.querySelector('textarea[name="description"]').value;
    const slug = document.querySelector('input[name="slug"]').value;

    const postData = {
        id: postId,
        thumbnailImage: thumbnailImage,
        description: description,
        inPrivate: inPrivate,
        slug: slug,
        publishStatus: true
    }

    fetch('/api/posts/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(postData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                console.error('No redirect URL provided');
                alert('저장은 완료되었지만 리다이렉트 URL이 없습니다.');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('저장 중 오류가 발생했습니다.');
        });
}

document.addEventListener('DOMContentLoaded', initializeEventListeners);