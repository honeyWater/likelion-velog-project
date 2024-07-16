let inPrivate = false; // 전역 변수로 선언

function initializeEventListeners() {
    const thumbnailUpload = document.getElementById('thumbnailUpload');
    const noPrivateButton = document.querySelector('.no_private');
    const yesPrivateButton = document.querySelector('.yes_private');
    const publishButton = document.querySelector('.real_publish');

    if (thumbnailUpload) {
        thumbnailUpload.addEventListener('change', handleThumbnailUpload);
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