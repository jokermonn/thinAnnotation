package com.joker.buildsrc

class Util {

    static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream()
        final byte[] buffer = new byte[8024]
        int n = 0
        long count = 0
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n)
            count += n
        }
        return output.toByteArray()
    }

    static void renameFile(File originFile, File targetFile) {
        if (targetFile.exists()) {
            targetFile.delete()
        }
        targetFile.parentFile.mkdirs()
        if (!originFile.renameTo(targetFile)) {
            throw new RuntimeException("${originFile} rename to ${targetFile} failed ")
        }

    }
}
