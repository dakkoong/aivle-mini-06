const MAX_COVER_UPLOAD_BYTES = 950 * 1024;

const loadImage = (src) =>
  new Promise((resolve, reject) => {
    const image = new Image();
    image.onload = () => resolve(image);
    image.onerror = () => reject(new Error("표지 이미지를 불러오지 못했습니다."));
    image.src = src;
  });

const compressCoverImage = async (imageSrc) => {
  if (
    !imageSrc?.startsWith("data:image/") ||
    new Blob([imageSrc]).size <= MAX_COVER_UPLOAD_BYTES
  ) {
    return imageSrc;
  }

  const image = await loadImage(imageSrc);
  const canvas = document.createElement("canvas");
  const context = canvas.getContext("2d");

  if (!context) {
    throw new Error("표지 이미지를 압축하지 못했습니다.");
  }

  const scale = Math.min(1, 1200 / Math.max(image.naturalWidth, image.naturalHeight));
  canvas.width = Math.round(image.naturalWidth * scale);
  canvas.height = Math.round(image.naturalHeight * scale);
  context.drawImage(image, 0, 0, canvas.width, canvas.height);

  for (let attempt = 0; attempt < 5; attempt += 1) {
    const quality = 0.8 - attempt * 0.1;
    const compressedImage = canvas.toDataURL("image/jpeg", quality);

    if (new Blob([compressedImage]).size <= MAX_COVER_UPLOAD_BYTES) {
      return compressedImage;
    }
  }

  throw new Error("압축된 표지 이미지가 업로드 제한을 초과했습니다.");
};

export default compressCoverImage;
