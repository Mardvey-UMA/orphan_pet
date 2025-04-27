import { Emoji, EmojiStyle } from 'emoji-picker-react'
import { CSSProperties, useEffect, useState } from 'react'

interface AnimatedEmojiProps {
	emoji: string
	size?: number
	animation?: 'bounce' | 'pulse' | 'spin' | 'float'
	speed?: number
	style?: CSSProperties
}

// Глобальный флаг для отслеживания добавленных стилей
const isStylesAdded = { current: false }

export const AnimatedEmoji = ({
	emoji,
	size = 24,
	animation = 'bounce',
	speed = 2,
	style = {},
}: AnimatedEmojiProps) => {
	const [key, setKey] = useState(0)

	useEffect(() => {
		if (!isStylesAdded.current) {
			const styleTag = document.createElement('style')
			styleTag.textContent = `
        @keyframes bounce {
          0%, 100% { transform: translateY(0); }
          50% { transform: translateY(-15px); }
        }
        @keyframes pulse {
          0%, 100% { transform: scale(1); }
          50% { transform: scale(1.2); }
        }
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
        @keyframes float {
          0%, 100% { transform: translateY(0) rotate(0deg); }
          25% { transform: translateY(-10px) rotate(5deg); }
          75% { transform: translateY(-10px) rotate(-5deg); }
        }
      `
			document.head.appendChild(styleTag)
			isStylesAdded.current = true
		}
	}, [])

	useEffect(() => {
		setKey(prev => prev + 1)
	}, [emoji, size, animation, speed])

	const animationConfig = {
		bounce: { name: 'bounce', timing: 'ease' },
		pulse: { name: 'pulse', timing: 'ease' },
		spin: { name: 'spin', timing: 'linear' },
		float: { name: 'float', timing: 'ease-in-out' },
	}[animation]

	return (
		<div
			key={key}
			style={{
				display: 'inline-block',
				animationName: animationConfig.name,
				animationDuration: `${speed}s`,
				animationIterationCount: 'infinite',
				animationTimingFunction: animationConfig.timing,
				...style,
			}}
		>
			<Emoji unified={emoji} size={size} emojiStyle={EmojiStyle.APPLE} />
		</div>
	)
}
