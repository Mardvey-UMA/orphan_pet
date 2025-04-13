import axios from 'axios'

export const uploadToS3 = async (presignedUrl: string, file: File) => {
	try {
		await axios.put(presignedUrl, file, {
			headers: {
				'Content-Type': file.type,
			},
		})
		return true
	} catch (error) {
		console.error('S3 upload failed:', error)
		return false
	}
}
