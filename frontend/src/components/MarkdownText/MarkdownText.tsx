import { Typography } from 'antd'
import React from 'react'
import ReactMarkdown from 'react-markdown'

const { Text } = Typography

interface MarkdownTextProps {
	content: string
	italic?: boolean
}

const MarkdownText: React.FC<MarkdownTextProps> = ({
	content,
	italic = false,
}) => {
	return (
		<Text italic={italic}>
			<ReactMarkdown>{content}</ReactMarkdown>
		</Text>
	)
}

export default MarkdownText
