document.addEventListener("DOMContentLoaded", function () {
    const initialContent = /*[[${post.content}]]*/ '';

    // 파일 입력 요소 생성
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';
    fileInput.style.display = 'none';
    document.body.appendChild(fileInput);

    // 디바운스 함수
    function debounce(func, wait) {
        let timeout;
        return function executeFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // SimpleMDE 초기화
    window.simplemde = new SimpleMDE({
        element: document.getElementById("content"),
        initialValue: initialContent,
        placeholder: "당신의 이야기를 적어보세요...",
        toolbar: [
            "heading-1", "heading-2", "heading-3", "|",
            "bold", "italic", "strikethrough", "|",
            "quote", "link", "image", "code", "table", "|",
        ],
        uploadImage: true,
        imageUploadFunction: uploadImage,
        previewRender: function(plainText, preview){
            // 이 함수는 미리보기를 렌더링할 때 호출된다.
            setTimeout(function(){
                preview.innerHTML = this.parent.markdown(plainText);
            }.bind(this), 1);
            return "로딩중...";
        },
        spellChecker: false,
        sideBySideFullscreen: false,
        status: false
    });

    // SimpleMDE의 미리보기를 오른쪽 패널로 이동
    document.querySelector('.write_right #preview').appendChild(
        document.querySelector('.editor-preview-side')
    );

    // 미리보기 업데이트 함수
    function updatePreview() {
        var preview = document.querySelector('.write_right #preview');
        preview.innerHTML = simplemde.markdown(simplemde.value());
    }

    // 디바운스된 미리보기 업데이트 함수
    const debounceUpdatePreview = debounce(updatePreview, 300);

    // 에디터 내용이 변경될 때마다 미리보기 업데이트
    simplemde.codemirror.on("change", debounceUpdatePreview);

    // 초기 미리보기 업데이트
    updatePreview();

    // 제목 실시간으로 미리보기 기능
    const titleArea = document.querySelector('.titleArea');
    const titlePreview = document.querySelector('.title_preview');
    titleArea.addEventListener('input', function() {
        titlePreview.textContent = this.value;
    })

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