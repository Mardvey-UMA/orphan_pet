import { Button, Upload, UploadProps } from 'antd'

interface FileUploadProps extends UploadProps {
	buttonText: string
	buttonIcon: React.ReactNode
}

export const FileUpload = ({
	buttonText,
	buttonIcon,
	...props
}: FileUploadProps) => {
	return (
		<Upload {...props}>
			<Button icon={buttonIcon}>{buttonText}</Button>
		</Upload>
	)
}
