document.addEventListener("DOMContentLoaded", function () {
    const initialContent = /*[[${post.content}]]*/ '';

    // 파일 입력 요소 생성
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';
    fileInput.style.display = 'none';
    document.body.appendChild(fileInput);

    // SimpleMDE 초기화
    window.simplemde = new SimpleMDE({
        element: document.getElementById("content"),
        initialValue: initialContent,
        toolbar: [
            "bold", "italic", "heading", "|",
            "quote", "unordered-list", "ordered-list", "|",
            "link", "image", "|",
            "preview", "side-by-side",
        ],
        uploadImage: true,
        imageUploadFunction: uploadImage
    });

    // drawImage 함수 오버라이드
    simplemde.toolbar.find(item => item.name === 'image').action = function customDrawImage(editor) {
        fileInput.click();
    };

    // 파일 선택 이벤트 처리
    fileInput.addEventListener('change', function (e) {
        const file = e.target.files[0];
        if (file) {
            uploadImage(file,
                url => {
                    const cursor = simplemde.codemirror.getCursor();
                    simplemde.codemirror.replaceRange(`![image](${url})`, cursor);
                },
                error => {
                    console.error('Image upload failed: ', error);
                    alert('Image uplaod failed: ', error);
                }
            );
        }
        // 파일 입력 초기화 (같은 파일을 다시 선택할 수 있도록)
        fileInput.value = '';
    });

    // 붙여넣기 이벤트 리스너 추가
    simplemde.codemirror.on("paste", function (editor, event) {
        const items = (event.clipboardData || event.originalEvent.clipboardData).items;
        for (let item of items) {
            if (item.type.indexOf("image") !== -1) {
                event.preventDefault();
                const blob = item.getAsFile();
                uploadImage(blob,
                    url => {
                        const cursor = editor.getCursor();
                        editor.replaceRange(`![image](${url})`, cursor);
                    },
                    error => {
                        console.error('Image upload failed: ', error);
                        alert('Image uplaod failed: ' + error);
                    }
                );
                break;
            }
        }
    });

    function uploadImage(file, onSuccess, onError) {
        if (file.size > 5 * 1024 * 1024) {  // 5MB 제한
            onError('File is too large. Maximum size is 5MB.');
        }
        if (!file.type.startsWith('image/')) {
            onError('File is not an image.');
            return;
        }
        const formData = new FormData();
        formData.append('image', file);

        fetch('/api/posts/upload-image', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.url) {
                    onSuccess(data.url);
                } else {
                    onError('Image upload failed');
                }
            })
            .catch(error => {
                console.error('Error: ', error);
                onError('Image upload failed');
            });
    }

    // temporary_save 버튼에 이벤트 리스너 추가
    document.querySelector('.temporary_save').addEventListener('click', function (e) {
        e.preventDefault();

        // hidden input에서 post.id 값을 가져옵니다.
        const postId = document.getElementById('postId').value;
        // 엔드포인트 결정
        const endpoint = postId ? '/api/posts/temporary-save' : '/api/posts/first-temporary-save';
        // 메소드 결정
        const method = postId ? 'PUT' : 'POST';

        // 데이터 준비
        const data = {
            id: postId || null,
            title: document.querySelector('.titleArea').value,
            content: simplemde.value(),
            thumbnailImage: 'justTemp'
        };

        // fetch를 사용하여 요청 보내기
        fetch(endpoint, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Server responded with an error!');
                }
                return response.json();
            })
            .then(data => {
                console.log('Success:', data);
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
    });

    // publish_post 버튼에 이벤트 리스너 추가
    document.querySelector('.publish_post').addEventListener('click', function (e) {
        e.preventDefault();

        // hidden input에서 post.id 값을 가져옵니다.
        const postId = document.getElementById('postId').value;
        // 엔드포인트 결정
        const endpoint = postId ? '/api/posts/temporary-save' : '/api/posts/first-temporary-save';
        // 메소드 결정
        const method = postId ? 'PUT' : 'POST';

        // 데이터 준비
        const data = {
            id: postId || null,
            title: document.querySelector('.titleArea').value,
            content: simplemde.value(),
            thumbnailImage: 'forPublish'
        };

        // fetch를 사용하여 요청 보내기
        fetch(endpoint, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Server responded with an error!');
                }
                return response.json();
            })
            .then(data => {
                console.log('Success:', data);
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
    });
});